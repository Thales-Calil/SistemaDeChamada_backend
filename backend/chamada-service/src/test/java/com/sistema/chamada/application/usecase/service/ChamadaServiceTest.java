package com.sistema.chamada.application.usecase.service;

import com.sistema.chamada.application.exception.NaoEncontradoException;
import com.sistema.chamada.application.exception.RegraNegocioException;
import com.sistema.chamada.application.ports.out.ChamadaEventPublisherPort;
import com.sistema.chamada.application.ports.out.ChamadaRepositoryPort;
import com.sistema.chamada.application.ports.out.PresencaRepositoryPort;
import com.sistema.chamada.application.ports.out.TurmaInfo;
import com.sistema.chamada.domain.event.ChamadaFinalizadaEvent;
import com.sistema.chamada.domain.model.Chamada;
import com.sistema.chamada.domain.model.StatusChamada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChamadaServiceTest {

    @Mock private ChamadaRepositoryPort chamadaRepositoryPort;
    @Mock private PresencaRepositoryPort presencaRepositoryPort;
    @Mock private ChamadaEventPublisherPort chamadaEventPublisherPort;
    @Mock private AcessoTurma acessoTurma;

    @InjectMocks
    private ChamadaService chamadaService;

    private Chamada chamada;
    private final TurmaInfo turma = new TurmaInfo(10L, "Turma A", 5L, "Cloud");

    @BeforeEach
    void setUp() {
        chamada = new Chamada();
        chamada.setId(1L);
        chamada.setTurmaId(10L);
        chamada.setData(LocalDate.of(2026, 11, 13));
        chamada.setHorario(LocalTime.of(19, 0));
        chamada.setStatus(StatusChamada.ABERTA);
    }

    @Test
    void deveCriarChamadaAberta() {
        when(acessoTurma.verificar(10L)).thenReturn(turma);
        when(chamadaRepositoryPort.existe(10L, chamada.getData(), chamada.getHorario())).thenReturn(false);
        when(chamadaRepositoryPort.salvar(any(Chamada.class))).thenReturn(chamada);

        Chamada resultado = chamadaService.criar(chamada);

        assertThat(resultado.getStatus()).isEqualTo(StatusChamada.ABERTA);
        verify(chamadaRepositoryPort).salvar(chamada);
    }

    @Test
    void naoCriaChamadaDuplicada() {
        when(acessoTurma.verificar(10L)).thenReturn(turma);
        when(chamadaRepositoryPort.existe(10L, chamada.getData(), chamada.getHorario())).thenReturn(true);

        assertThatThrownBy(() -> chamadaService.criar(chamada)).isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void chamadaInexistenteRetorna404() {
        when(chamadaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> chamadaService.buscarPorId(99L)).isInstanceOf(NaoEncontradoException.class);
    }

    @Test
    void deveFinalizarChamadaEGravarEventoNoOutbox() {
        when(chamadaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(chamada));
        when(acessoTurma.verificar(10L)).thenReturn(turma);
        when(chamadaRepositoryPort.salvar(any(Chamada.class))).thenReturn(chamada);

        chamadaService.finalizar(1L);

        assertThat(chamada.getStatus()).isEqualTo(StatusChamada.FINALIZADA);
        verify(chamadaEventPublisherPort, times(1)).publicarChamadaFinalizada(any(ChamadaFinalizadaEvent.class));
    }

    @Test
    void finalizarDuasVezesNaoDuplicaEvento() {
        chamada.setStatus(StatusChamada.FINALIZADA);
        when(chamadaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(chamada));
        when(acessoTurma.verificar(10L)).thenReturn(turma);

        chamadaService.finalizar(1L);

        verify(chamadaRepositoryPort, never()).salvar(any());
        verifyNoInteractions(chamadaEventPublisherPort);
    }

    @Test
    void naoExcluiChamadaFinalizada() {
        chamada.setStatus(StatusChamada.FINALIZADA);
        when(chamadaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(chamada));
        when(acessoTurma.verificar(10L)).thenReturn(turma);

        assertThatThrownBy(() -> chamadaService.excluir(1L)).isInstanceOf(RegraNegocioException.class);
        verify(chamadaRepositoryPort, never()).excluir(any());
    }
}
