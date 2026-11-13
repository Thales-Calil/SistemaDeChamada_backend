package com.sistema.chamada.infrastructure.adapters.out.messaging;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.sistema.chamada.infrastructure.adapters.out.persistence.entity.OutboxEventoEntity;
import com.sistema.chamada.infrastructure.adapters.out.persistence.repository.SpringDataOutboxRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Envia ao Service Bus os eventos pendentes da tabela outbox_eventos (a cada 5 s).
 * - MessageId = chave do evento (o Service Bus descarta duplicatas se "Duplicate detection" estiver ligado
 *   no tópico; os consumidores também são idempotentes).
 * - Falha de envio: o evento continua pendente e é tentado de novo no próximo ciclo.
 */
@Slf4j
@Component
public class OutboxRelay {

    private final SpringDataOutboxRepository outboxRepository;
    private final ObjectProvider<ServiceBusSenderClient> senderProvider;
    private boolean avisouSemServiceBus;

    public OutboxRelay(SpringDataOutboxRepository outboxRepository, ObjectProvider<ServiceBusSenderClient> senderProvider) {
        this.outboxRepository = outboxRepository;
        this.senderProvider = senderProvider;
    }

    @Scheduled(fixedDelayString = "${outbox.intervalo-ms:5000}", initialDelayString = "${outbox.atraso-inicial-ms:10000}")
    public void enviarPendentes() {
        ServiceBusSenderClient sender = senderProvider.getIfAvailable();
        if (sender == null) {
            if (!avisouSemServiceBus) {
                log.warn("Service Bus desligado (AZURE_SERVICEBUS_ENABLED=false): eventos ficam pendentes na tabela outbox_eventos.");
                avisouSemServiceBus = true;
            }
            return;
        }

        List<OutboxEventoEntity> pendentes = outboxRepository.findTop50ByEnviadoEmIsNullOrderByIdAsc();
        for (OutboxEventoEntity evento : pendentes) {
            try {
                sender.sendMessage(montarMensagem(evento));
                evento.setEnviadoEm(LocalDateTime.now());
                evento.setUltimoErro(null);
                outboxRepository.save(evento);
                log.info("Evento {} enviado ao Service Bus.", evento.getChave());
            } catch (Exception e) {
                evento.setTentativas(evento.getTentativas() + 1);
                String erro = String.valueOf(e.getMessage());
                evento.setUltimoErro(erro.length() > 1000 ? erro.substring(0, 1000) : erro);
                outboxRepository.save(evento);
                log.error("Falha ao enviar o evento {} (tentativa {}). Nova tentativa no próximo ciclo.",
                        evento.getChave(), evento.getTentativas(), e);
                return; // Service Bus com problema: não insiste no resto agora
            }
        }
    }

    static ServiceBusMessage montarMensagem(OutboxEventoEntity evento) {
        ServiceBusMessage mensagem = new ServiceBusMessage(evento.getPayload());
        mensagem.setMessageId(evento.getChave());
        mensagem.setContentType("application/json");
        mensagem.setSubject("ChamadaFinalizada");
        mensagem.getApplicationProperties().put("eventType", evento.getTipo());
        return mensagem;
    }
}
