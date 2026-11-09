package com.sistema.aluno.infrastructure.adapters.in.messaging;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sistema.aluno.application.ports.in.ProcessarChamadaFinalizadaUseCase;
import com.sistema.aluno.domain.event.ChamadaFinalizadaEvent;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AzureServiceBusListenerAdapter {

    @Value("${azure.servicebus.connection-string}")
    private String connectionString;

    @Value("${azure.servicebus.topic-name:chamadas-topic}")
    private String topicName;

    @Value("${azure.servicebus.subscription-name:aluno-service-sub}")
    private String subscriptionName;

    private final ServiceBusClientBuilder clientBuilder; // Injetado a partir do ServiceBusConfig
    private final ProcessarChamadaFinalizadaUseCase processarChamadaFinalizadaUseCase;
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
    private ServiceBusProcessorClient processorClient;

    @PostConstruct
    public void iniciarConsumer() {
        if (connectionString.contains("mock.servicebus")) {
            log.warn("Azure Service Bus MOCK detectado. O listener não será iniciado em ambiente de testes locais.");
            return;
        }

        this.processorClient = clientBuilder.processor()
                .topicName(topicName)
                .subscriptionName(subscriptionName)
                .processMessage(this::processarMensagem)
                .processError(this::processarErro)
                .buildProcessorClient();

        this.processorClient.start();
        log.info("Consumer do Azure Service Bus ativado [Tópico: {}, Subscription: {}]", topicName, subscriptionName);
    }

    private void processarMensagem(ServiceBusReceivedMessageContext context) {
        try {
            String payload = context.getMessage().getBody().toString();
            ChamadaFinalizadaEvent evento = objectMapper.readValue(payload, ChamadaFinalizadaEvent.class);

            processarChamadaFinalizadaUseCase.processar(evento);
        } catch (Exception e) {
            log.error("Erro no processamento da mensagem do Service Bus: {}", e.getMessage(), e);
        }
    }

    private void processarErro(ServiceBusErrorContext context) {
        log.error("Exceção capturada na escuta do Azure Service Bus. Entidade: {}", context.getEntityPath(), context.getException());
    }

    @PreDestroy
    public void pararConsumer() {
        if (processorClient != null && processorClient.isRunning()) {
            processorClient.close();
            log.info("Consumer do Azure Service Bus desconectado com sucesso.");
        }
    }
}