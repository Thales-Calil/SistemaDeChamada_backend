package com.sistema.aluno.infrastructure.adapters.in.messaging;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.azure.messaging.servicebus.models.DeadLetterOptions;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sistema.aluno.application.ports.in.ProcessarChamadaFinalizadaUseCase;
import com.sistema.aluno.domain.event.ChamadaFinalizadaEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * Consumidor do tópico de chamadas (2.4).
 * - A mensagem só é confirmada (complete) DEPOIS de processada com sucesso.
 * - Falha no processamento -> abandon: o Service Bus entrega de novo e, após o MaxDeliveryCount
 *   da subscription (padrão 10), move para a Dead-Letter Queue. Nada se perde em silêncio.
 * - JSON inválido -> vai direto para a Dead-Letter Queue (não adianta tentar de novo).
 * Desligado com AZURE_SERVICEBUS_ENABLED=false (ex.: rodar localmente sem Service Bus).
 */
@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "azure.servicebus.enabled", havingValue = "true", matchIfMissing = true)
public class AzureServiceBusListenerAdapter {

    @Value("${azure.servicebus.topic-name:chamadas-topic}")
    private String topicName;

    @Value("${azure.servicebus.subscription-name:aluno-service-sub}")
    private String subscriptionName;

    private final ServiceBusClientBuilder clientBuilder;
    private final ProcessarChamadaFinalizadaUseCase processarChamadaFinalizadaUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    private ServiceBusProcessorClient processorClient;

    @PostConstruct
    public void iniciarConsumer() {
        this.processorClient = clientBuilder.processor()
                .topicName(topicName)
                .subscriptionName(subscriptionName)
                .disableAutoComplete()
                .maxConcurrentCalls(1)
                .processMessage(this::processarMensagem)
                .processError(this::processarErro)
                .buildProcessorClient();

        this.processorClient.start();
        log.info("Consumer do Azure Service Bus ativado [Tópico: {}, Subscription: {}]", topicName, subscriptionName);
    }

    void processarMensagem(ServiceBusReceivedMessageContext context) {
        ServiceBusReceivedMessage mensagem = context.getMessage();
        ChamadaFinalizadaEvent evento;
        try {
            evento = objectMapper.readValue(mensagem.getBody().toString(), ChamadaFinalizadaEvent.class);
        } catch (Exception e) {
            log.error("Mensagem {} com JSON inválido. Enviando para a Dead-Letter Queue.", mensagem.getMessageId(), e);
            context.deadLetter(new DeadLetterOptions()
                    .setDeadLetterReason("PayloadInvalido")
                    .setDeadLetterErrorDescription(resumir(e.getMessage())));
            return;
        }

        try {
            processarChamadaFinalizadaUseCase.processar(evento);
            context.complete();
        } catch (Exception e) {
            log.error("Falha ao processar a mensagem {} (tentativa {}). Será entregue novamente.",
                    mensagem.getMessageId(), mensagem.getDeliveryCount(), e);
            context.abandon();
        }
    }

    private void processarErro(ServiceBusErrorContext context) {
        log.error("Erro na escuta do Azure Service Bus. Entidade: {}", context.getEntityPath(), context.getException());
    }

    private static String resumir(String texto) {
        if (texto == null) return "";
        return texto.length() > 1000 ? texto.substring(0, 1000) : texto;
    }

    @PreDestroy
    public void pararConsumer() {
        if (processorClient != null) {
            processorClient.close();
            log.info("Consumer do Azure Service Bus desconectado.");
        }
    }
}
