package com.sistema.chamada.infrastructure.adapters.out.messaging;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.chamada.application.ports.out.ChamadaEventPublisherPort;
import com.sistema.chamada.domain.event.ChamadaFinalizadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AzureServiceBusPublisherAdapter implements ChamadaEventPublisherPort {

    private final ServiceBusSenderClient senderClient;
    private final ObjectMapper objectMapper;

    @Override
    public void publicarChamadaFinalizada(ChamadaFinalizadaEvent evento) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(evento);
            ServiceBusMessage message = new ServiceBusMessage(jsonPayload);
            message.setSubject("ChamadaFinalizada");
            message.getApplicationProperties().put("eventType", "CHAMADA_FINALIZADA");

            senderClient.sendMessage(message);
            log.info("Evento ChamadaFinalizada publicado com sucesso. ID Chamada: {}", evento.getChamadaId());
        } catch (Exception e) {
            log.error("Erro ao serializar ou enviar mensagem para o Azure Service Bus: {}", e.getMessage(), e);
            throw new RuntimeException("Falha na publicação do evento de chamada finalizada", e);
        }
    }
}