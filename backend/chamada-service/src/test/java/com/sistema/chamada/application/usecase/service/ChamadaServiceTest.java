package com.sistema.chamada.application.usecase.service;

import com.sistema.chamada.application.ports.out.ChamadaRepositoryPort;
import com.sistema.chamada.domain.model.Chamada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChamadaServiceTest {

    @Mock
    private ChamadaRepositoryPort chamadaRepositoryPort;

    @InjectMocks
    private ChamadaService chamadaService;

    private Chamada chamada;

    @BeforeEach
    void setUp() {
        chamada = new Chamada();
        chamada.setId(1L);
        chamada.setTurmaId(10L);
        chamada.setData(LocalDate.now());
    }

    @Test
    void deveCriarChamada() {
        when(chamadaRepositoryPort.salvar(any(Chamada.class))).thenReturn(chamada);

        Chamada resultado = chamadaService.criar(chamada);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        verify(chamadaRepositoryPort, times(1)).salvar(chamada);
    }

    @Test
    void deveBuscarChamadaPorId() {
        when(chamadaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(chamada));

        Chamada resultado = chamadaService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getTurmaId()).isEqualTo(10L);
        verify(chamadaRepositoryPort, times(1)).buscarPorId(1L);
    }
}