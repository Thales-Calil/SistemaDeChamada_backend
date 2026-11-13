package com.sistema.aluno.infrastructure.config;

import com.azure.messaging.servicebus.ServiceBusClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Sem valor "mock" padrão: se o Service Bus estiver ligado (padrão) e a variável
 * AZURE_SERVICEBUS_CONNECTION_STRING não existir, a aplicação NÃO sobe - o erro aparece
 * no deploy, e não em silêncio em produção.
 */
@Configuration
@ConditionalOnProperty(name = "azure.servicebus.enabled", havingValue = "true", matchIfMissing = true)
public class ServiceBusConfig {

    @Bean
    public ServiceBusClientBuilder serviceBusClientBuilder(@Value("${azure.servicebus.connection-string}") String connectionString) {
        if (connectionString == null || connectionString.isBlank()) {
            throw new IllegalStateException("AZURE_SERVICEBUS_CONNECTION_STRING não configurada "
                    + "(ou defina AZURE_SERVICEBUS_ENABLED=false para rodar sem Service Bus).");
        }
        return new ServiceBusClientBuilder().connectionString(connectionString);
    }
}
