package com.sistema.aluno.infrastructure.adapters.in.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sistema.aluno.application.ports.in.ProcessarChamadaFinalizadaUseCase;
import com.sistema.aluno.domain.event.ChamadaFinalizadaEvent;
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
class AzureServiceBusListenerAdapterTest {

    @Mock
    private ProcessarChamadaFinalizadaUseCase processarChamadaFinalizadaUseCase;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void deveDesserializarEProcessarMensagemCorretamente() throws Exception {
        ChamadaFinalizadaEvent eventoOriginal = new ChamadaFinalizadaEvent(1L, 10L, LocalDate.now(), "FINALIZADA", null);
        String jsonPayload = objectMapper.writeValueAsString(eventoOriginal);

        ChamadaFinalizadaEvent eventoDesserializado = objectMapper.readValue(jsonPayload, ChamadaFinalizadaEvent.class);
        processarChamadaFinalizadaUseCase.processar(eventoDesserializado);

        ArgumentCaptor<ChamadaFinalizadaEvent> captor = ArgumentCaptor.forClass(ChamadaFinalizadaEvent.class);
        verify(processarChamadaFinalizadaUseCase, times(1)).processar(captor.capture());

        assertThat(captor.getValue().getChamadaId()).isEqualTo(1L);
        assertThat(captor.getValue().getTurmaId()).isEqualTo(10L);
    }
}