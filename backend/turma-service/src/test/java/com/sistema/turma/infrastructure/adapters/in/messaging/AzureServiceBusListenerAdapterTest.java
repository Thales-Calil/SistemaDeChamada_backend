package com.sistema.turma.infrastructure.adapters.in.messaging;

import com.azure.core.util.BinaryData;
import com.azure.messaging.servicebus.ServiceBusReceivedMessage;
import com.azure.messaging.servicebus.ServiceBusReceivedMessageContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sistema.turma.application.ports.in.ProcessarChamadaFinalizadaUseCase;
import com.sistema.turma.domain.event.ChamadaFinalizadaEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AzureServiceBusListenerAdapterTest {

    @Mock
    private ProcessarChamadaFinalizadaUseCase processarChamadaFinalizadaUseCase;

    @Mock
    private ServiceBusReceivedMessageContext context;

    @Mock
    private ServiceBusReceivedMessage message;

    private AzureServiceBusListenerAdapter listenerAdapter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());
        listenerAdapter = new AzureServiceBusListenerAdapter(null, processarChamadaFinalizadaUseCase);
    }

    @Test
    void deveProcessarMensagemRecebidaDoServiceBus() throws Exception {
        ChamadaFinalizadaEvent eventoOriginal = new ChamadaFinalizadaEvent(1L, 10L, LocalDate.now(), "FINALIZADA", null);
        String jsonPayload = objectMapper.writeValueAsString(eventoOriginal);

        when(context.getMessage()).thenReturn(message);
        when(message.getBody()).thenReturn(BinaryData.fromString(jsonPayload));

        listenerAdapter.processarMensagem(context);

        ArgumentCaptor<ChamadaFinalizadaEvent> captor = ArgumentCaptor.forClass(ChamadaFinalizadaEvent.class);
        verify(processarChamadaFinalizadaUseCase, times(1)).processar(captor.capture());

        assertThat(captor.getValue().getChamadaId()).isEqualTo(1L);
        assertThat(captor.getValue().getTurmaId()).isEqualTo(10L);
    }
}