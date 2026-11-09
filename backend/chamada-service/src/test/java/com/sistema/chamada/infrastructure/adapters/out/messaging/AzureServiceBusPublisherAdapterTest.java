package com.sistema.chamada.infrastructure.adapters.out.messaging;

import com.azure.messaging.servicebus.ServiceBusMessage;
import com.azure.messaging.servicebus.ServiceBusSenderClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sistema.chamada.domain.event.ChamadaFinalizadaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AzureServiceBusPublisherAdapterTest {

    @Mock
    private ServiceBusSenderClient senderClient;

    private AzureServiceBusPublisherAdapter publisherAdapter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        publisherAdapter = new AzureServiceBusPublisherAdapter(senderClient, objectMapper);
    }

    @Test
    void deveSerializarEPublicarEventoComSucesso() {
        ChamadaFinalizadaEvent evento = new ChamadaFinalizadaEvent(1L, 10L, LocalDate.now(), "FINALIZADA");

        publisherAdapter.publicarChamadaFinalizada(evento);

        ArgumentCaptor<ServiceBusMessage> captor = ArgumentCaptor.forClass(ServiceBusMessage.class);
        verify(senderClient, times(1)).sendMessage(captor.capture());

        ServiceBusMessage mensagemEnviada = captor.getValue();
        assertThat(mensagemEnviada.getSubject()).isEqualTo("ChamadaFinalizada");
        assertThat(mensagemEnviada.getApplicationProperties().get("eventType")).isEqualTo("CHAMADA_FINALIZADA");
        assertThat(mensagemEnviada.getBody().toString()).contains("\"chamadaId\":1");
    }
}