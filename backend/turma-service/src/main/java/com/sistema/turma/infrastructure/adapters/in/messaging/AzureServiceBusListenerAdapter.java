package com.sistema.turma.infrastructure.adapters.in.messaging;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusErrorContext;
import com.azure.messaging.servicebus.ServiceBusProcessorClient;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sistema.turma.application.ports.in.ProcessarChamadaFinalizadaUseCase;
import com.sistema.turma.domain.event.ChamadaFinalizadaEvent;
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

    @Value("${azure.servicebus.subscription-name:turma-service-sub}")
    private String subscriptionName;

    private final ServiceBusClientBuilder clientBuilder;
    private final ProcessarChamadaFinalizadaUseCase processarChamadaFinalizadaUseCase;

    // Instanciação direta com módulo de datas Java 8 (mesmo padrão do aluno-service)
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    private ServiceBusProcessorClient processorClient;

    @PostConstruct
    public void iniciarConsumer() {
        if (connectionString.contains("mock.servicebus")) {
            log.warn("Azure Service Bus MOCK detectado. Consumer do TurmaService não será iniciado localmente.");
            return;
        }

        this.processorClient = clientBuilder.processor()
                .topicName(topicName)
                .subscriptionName(subscriptionName)
                .processMessage(this::processarMensagem)
                .processError(this::processarErro)
                .buildProcessorClient();

        this.processorClient.start();
        log.info("Consumer do Azure Service Bus ativado no TurmaService [Tópico: {}, Subscription: {}]",
                topicName, subscriptionName);
    }

    void processarMensagem(ServiceBusReceivedMessageContext context) {
        try {
            String payload = context.getMessage().getBody().toString();
            ChamadaFinalizadaEvent evento = objectMapper.readValue(payload, ChamadaFinalizadaEvent.class);

            processarChamadaFinalizadaUseCase.processar(evento);
        } catch (Exception e) {
            log.error("Erro no processamento da mensagem no TurmaService: {}", e.getMessage(), e);
        }
    }

    private void processarErro(ServiceBusErrorContext context) {
        log.error("Erro na escuta do Azure Service Bus no TurmaService. Entidade: {}",
                context.getEntityPath(), context.getException());
    }

    @PreDestroy
    public void pararConsumer() {
        if (processorClient != null && processorClient.isRunning()) {
            processorClient.close();
            log.info("Consumer do Azure Service Bus desconectado no TurmaService.");
        }
    }
}