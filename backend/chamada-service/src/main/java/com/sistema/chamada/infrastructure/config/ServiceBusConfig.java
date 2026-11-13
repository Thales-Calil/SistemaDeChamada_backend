package com.sistema.chamada.infrastructure.config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sem valor "mock" padrão: com o Service Bus ligado (padrão) e sem AZURE_SERVICEBUS_CONNECTION_STRING,
 * a aplicação não sobe (o erro aparece no deploy, não em produção).
 */
@Configuration
@ConditionalOnProperty(name = "azure.servicebus.enabled", havingValue = "true", matchIfMissing = true)
public class ServiceBusConfig {

    @Bean(destroyMethod = "close")
    public ServiceBusSenderClient serviceBusSenderClient(
            @Value("${azure.servicebus.connection-string}") String connectionString,
            @Value("${azure.servicebus.topic-name:chamadas-topic}") String topicName) {
        if (connectionString == null || connectionString.isBlank()) {
            throw new IllegalStateException("AZURE_SERVICEBUS_CONNECTION_STRING não configurada "
                    + "(ou defina AZURE_SERVICEBUS_ENABLED=false para rodar sem Service Bus).");
        }
        return new ServiceBusClientBuilder()
                .connectionString(connectionString)
                .sender()
                .topicName(topicName)
                .buildClient();
    }
}
