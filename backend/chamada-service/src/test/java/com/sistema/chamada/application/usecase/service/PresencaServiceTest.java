package com.sistema.chamada.application.usecase.service;

import com.sistema.chamada.application.exception.AcessoNegadoException;
import com.sistema.chamada.application.exception.RegraNegocioException;
import com.sistema.chamada.application.ports.in.GerenciarChamadaUseCase;
import com.sistema.chamada.application.ports.in.ItemPresenca;
import com.sistema.chamada.application.ports.out.*;
import com.sistema.chamada.application.security.Perfil;
import com.sistema.chamada.application.security.UsuarioLogado;
import com.sistema.chamada.domain.model.Chamada;
import com.sistema.chamada.domain.model.FrequenciaAluno;
import com.sistema.chamada.domain.model.Presenca;
import com.sistema.chamada.domain.model.StatusChamada;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PresencaServiceTest {

    @Mock private ChamadaRepositoryPort chamadaRepositoryPort;
    @Mock private PresencaRepositoryPort presencaRepositoryPort;
    @Mock private TurmaServicePort turmaServicePort;
    @Mock private UsuarioLogadoPort usuarioLogadoPort;
    @Mock private AcessoTurma acessoTurma;
    @Mock private GerenciarChamadaUseCase gerenciarChamadaUseCase;

    @InjectMocks
    private PresencaService presencaService;

    private Chamada chamada;

    @BeforeEach
    void setUp() {
        chamada = new Chamada();
        chamada.setId(1L);
        chamada.setTurmaId(10L);
        chamada.setStatus(StatusChamada.ABERTA);
    }

    private static Chamada finalizada(long id) {
        Chamada c = new Chamada();
        c.setId(id);
        c.setTurmaId(10L);
        c.setStatus(StatusChamada.FINALIZADA);
        return c;
    }

    private static Presenca presenca(long chamadaId, long alunoId, boolean presente) {
        Presenca p = new Presenca();
        p.setChamadaId(chamadaId);
        p.setAlunoId(alunoId);
        p.setPresente(presente);
        return p;
    }

    @Test
    void loteGravaTodosEFinaliza() {
        when(chamadaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(chamada));
        when(turmaServicePort.alunosDaTurma(10L)).thenReturn(List.of(42L, 43L));
        when(presencaRepositoryPort.buscarPorChamadaEAluno(eq(1L), any())).thenReturn(Optional.empty());
        when(presencaRepositoryPort.salvar(any(Presenca.class))).thenAnswer(inv -> inv.getArgument(0));

        List<Presenca> gravadas = presencaService.registrarEmLote(1L,
                List.of(new ItemPresenca(42L, true), new ItemPresenca(43L, false)), true);

        assertThat(gravadas).hasSize(2);
        verify(gerenciarChamadaUseCase).finalizar(1L);
    }

    @Test
    void loteRecusaAlunoForaDaTurma() {
        when(chamadaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(chamada));
        when(turmaServicePort.alunosDaTurma(10L)).thenReturn(List.of(42L));

        assertThatThrownBy(() -> presencaService.registrarEmLote(1L,
                List.of(new ItemPresenca(42L, true), new ItemPresenca(99L, true)), false))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("99");
        verify(presencaRepositoryPort, never()).salvar(any());
    }

    @Test
    void loteRecusaAlunoRepetido() {
        assertThatThrownBy(() -> presencaService.registrarEmLote(1L,
                List.of(new ItemPresenca(42L, true), new ItemPresenca(42L, false)), false))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void naoAlteraChamadaFinalizada() {
        when(chamadaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(finalizada(1L)));

        assertThatThrownBy(() -> presencaService.registrarPresenca(1L, 42L, true))
                .isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void frequenciaIncluiAlunoSemNenhumRegistro() {
        when(chamadaRepositoryPort.listarPorTurma(10L)).thenReturn(List.of(finalizada(1L), finalizada(2L)));
        when(presencaRepositoryPort.buscarPorChamadas(List.of(1L, 2L))).thenReturn(List.of(
                presenca(1L, 42L, true), presenca(2L, 42L, true)));
        when(turmaServicePort.alunosDaTurma(10L)).thenReturn(List.of(42L, 43L));

        List<FrequenciaAluno> frequencias = presencaService.calcularPorTurma(10L);

        assertThat(frequencias).hasSize(2);
        assertThat(frequencias.get(0).getPercentual()).isEqualTo(100.0);
        assertThat(frequencias.get(1).getAlunoId()).isEqualTo(43L);
        assertThat(frequencias.get(1).getFaltas()).isEqualTo(2);
        assertThat(frequencias.get(1).isAbaixoDoMinimo()).isTrue();
    }

    @Test
    void alunoNaoVeFrequenciaDeOutro() {
        when(usuarioLogadoPort.atual()).thenReturn(new UsuarioLogado(3L, "aluno", Perfil.ALUNO, 42L));
        assertThatThrownBy(() -> presencaService.calcularPorAluno(43L)).isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void alunoVeAPropriaFrequenciaPorTurma() {
        when(usuarioLogadoPort.atual()).thenReturn(new UsuarioLogado(3L, "aluno", Perfil.ALUNO, 42L));
        when(turmaServicePort.turmasDoAluno(42L)).thenReturn(List.of(new TurmaInfo(10L, "Turma A", 5L, "Cloud")));
        when(chamadaRepositoryPort.listarPorTurma(10L)).thenReturn(List.of(finalizada(1L), finalizada(2L)));
        when(presencaRepositoryPort.buscarPorChamadas(List.of(1L, 2L))).thenReturn(List.of(
                presenca(1L, 42L, true), presenca(2L, 42L, false), presenca(1L, 43L, true)));

        var resultado = presencaService.calcularPorAluno(42L);

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).turmaNome()).isEqualTo("Turma A");
        assertThat(resultado.get(0).frequencia().getPercentual()).isEqualTo(50.0);
    }
}
