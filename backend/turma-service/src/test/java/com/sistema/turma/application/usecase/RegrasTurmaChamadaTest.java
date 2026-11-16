package com.sistema.turma.application.usecase;

import com.sistema.turma.application.exception.AcessoNegadoException;
import com.sistema.turma.application.exception.RegraNegocioException;
import com.sistema.turma.application.ports.in.GerenciarChamadaUseCase;
import com.sistema.turma.application.ports.in.GerenciarMatriculaUseCase;
import com.sistema.turma.application.ports.in.ItemPresenca;
import com.sistema.turma.application.ports.out.*;
import com.sistema.turma.application.security.Perfil;
import com.sistema.turma.application.security.UsuarioLogado;
import com.sistema.turma.application.usecase.service.AcessoTurma;
import com.sistema.turma.application.usecase.service.ChamadaService;
import com.sistema.turma.application.usecase.service.PresencaService;
import com.sistema.turma.application.usecase.service.TurmaService;
import com.sistema.turma.domain.event.ChamadaFinalizadaEvent;
import com.sistema.turma.domain.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Regras do turma-service unificado (turmas + chamadas), agora resolvidas localmente, sem HTTP entre serviços.
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class RegrasTurmaChamadaTest {

    @Mock private TurmaRepositoryPort turmaRepositoryPort;
    @Mock private ProfessorRepositoryPort professorRepositoryPort;
    @Mock private DisciplinaRepositoryPort disciplinaRepositoryPort;
    @Mock private MatriculaRepositoryPort matriculaRepositoryPort;
    @Mock private ChamadaRepositoryPort chamadaRepositoryPort;
    @Mock private PresencaRepositoryPort presencaRepositoryPort;
    @Mock private ChamadaEventPublisherPort chamadaEventPublisherPort;
    @Mock private UsuarioLogadoPort usuarioLogadoPort;
    @Mock private GerenciarMatriculaUseCase gerenciarMatriculaUseCase;
    @Mock private GerenciarChamadaUseCase gerenciarChamadaUseCase;

    private static final UsuarioLogado ADMIN = new UsuarioLogado(1L, "admin", Perfil.ADMIN, null);
    private static final UsuarioLogado PROFESSOR_5 = new UsuarioLogado(2L, "prof", Perfil.PROFESSOR, 5L);
    private static final UsuarioLogado PROFESSOR_9 = new UsuarioLogado(3L, "outro", Perfil.PROFESSOR, 9L);

    private AcessoTurma acessoTurma;
    private Turma turma;
    private Chamada chamadaAberta;

    @BeforeEach
    void setUp() {
        acessoTurma = new AcessoTurma(turmaRepositoryPort, usuarioLogadoPort);

        Professor professor = new Professor();
        professor.setId(5L);
        Disciplina disciplina = new Disciplina();
        disciplina.setId(10L);
        disciplina.setNome("Arquitetura Cloud");
        turma = new Turma();
        turma.setId(1L);
        turma.setNome("Turma A");
        turma.setProfessor(professor);
        turma.setDisciplina(disciplina);

        chamadaAberta = new Chamada();
        chamadaAberta.setId(100L);
        chamadaAberta.setTurmaId(1L);
        chamadaAberta.setData(LocalDate.of(2026, 11, 16));
        chamadaAberta.setStatus(StatusChamada.ABERTA);

        when(turmaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(turma));
    }

    private ChamadaService chamadaService() {
        return new ChamadaService(chamadaRepositoryPort, presencaRepositoryPort, chamadaEventPublisherPort,
                turmaRepositoryPort, acessoTurma);
    }

    private PresencaService presencaService() {
        return new PresencaService(chamadaRepositoryPort, presencaRepositoryPort, matriculaRepositoryPort,
                gerenciarMatriculaUseCase, usuarioLogadoPort, acessoTurma, gerenciarChamadaUseCase);
    }

    /** 1. Turma com chamadas não pode ser excluída (consulta local ao repositório de chamadas). */
    @Test
    void naoExcluiTurmaComChamadas() {
        when(usuarioLogadoPort.atual()).thenReturn(ADMIN);
        when(turmaRepositoryPort.existePorId(1L)).thenReturn(true);
        when(chamadaRepositoryPort.existePorTurma(1L)).thenReturn(true);

        TurmaService turmaService = new TurmaService(turmaRepositoryPort, professorRepositoryPort,
                disciplinaRepositoryPort, matriculaRepositoryPort, chamadaRepositoryPort, usuarioLogadoPort);

        assertThatThrownBy(() -> turmaService.excluir(1L)).isInstanceOf(RegraNegocioException.class);
        verify(turmaRepositoryPort, never()).excluir(any());
    }

    /** 2. Professor não registra chamada em turma de outro professor. */
    @Test
    void professorNaoAcessaTurmaDeOutroProfessor() {
        when(usuarioLogadoPort.atual()).thenReturn(PROFESSOR_9);

        assertThatThrownBy(() -> acessoTurma.verificar(1L)).isInstanceOf(AcessoNegadoException.class);
    }

    /** 3. Presença em lote só aceita alunos matriculados na turma (turma_alunos, local). Nada é gravado. */
    @Test
    void loteRecusaAlunoQueNaoEstaMatriculado() {
        when(usuarioLogadoPort.atual()).thenReturn(PROFESSOR_5);
        when(chamadaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(chamadaAberta));
        when(matriculaRepositoryPort.listarAlunoIds(1L)).thenReturn(List.of(42L));

        assertThatThrownBy(() -> presencaService().registrarEmLote(100L,
                List.of(new ItemPresenca(42L, true), new ItemPresenca(99L, true)), true))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("99");
        verify(presencaRepositoryPort, never()).salvar(any());
    }

    /** 4. Finalizar a chamada soma a aula ministrada da turma e grava o evento no Outbox, na mesma transação. */
    @Test
    void finalizarSomaAulaMinistradaEGravaEventoNoOutbox() {
        when(usuarioLogadoPort.atual()).thenReturn(PROFESSOR_5);
        when(chamadaRepositoryPort.buscarPorId(100L)).thenReturn(Optional.of(chamadaAberta));
        when(chamadaRepositoryPort.salvar(any(Chamada.class))).thenAnswer(inv -> inv.getArgument(0));

        chamadaService().finalizar(100L);

        assertThat(chamadaAberta.getStatus()).isEqualTo(StatusChamada.FINALIZADA);
        verify(turmaRepositoryPort).incrementarAulasMinistradas(1L);
        verify(chamadaEventPublisherPort).publicarChamadaFinalizada(any(ChamadaFinalizadaEvent.class));
    }

    /** 5. Frequência da turma inclui o aluno matriculado que nunca teve presença (RN06-RN08). */
    @Test
    void frequenciaIncluiAlunoSemRegistroEAlertaAbaixoDe75() {
        when(usuarioLogadoPort.atual()).thenReturn(ADMIN);
        Chamada c1 = new Chamada();
        c1.setId(1L);
        c1.setStatus(StatusChamada.FINALIZADA);
        Chamada c2 = new Chamada();
        c2.setId(2L);
        c2.setStatus(StatusChamada.FINALIZADA);
        Presenca p1 = new Presenca();
        p1.setChamadaId(1L);
        p1.setAlunoId(42L);
        p1.setPresente(true);
        Presenca p2 = new Presenca();
        p2.setChamadaId(2L);
        p2.setAlunoId(42L);
        p2.setPresente(true);
        when(chamadaRepositoryPort.listarPorTurma(1L)).thenReturn(List.of(c1, c2));
        when(presencaRepositoryPort.buscarPorChamadas(List.of(1L, 2L))).thenReturn(List.of(p1, p2));
        when(matriculaRepositoryPort.listarAlunoIds(1L)).thenReturn(List.of(42L, 43L));

        List<FrequenciaAluno> frequencias = presencaService().calcularPorTurma(1L);

        assertThat(frequencias).hasSize(2);
        assertThat(frequencias.get(0).getPercentual()).isEqualTo(100.0);
        assertThat(frequencias.get(1).getAlunoId()).isEqualTo(43L);
        assertThat(frequencias.get(1).getFaltas()).isEqualTo(2);
        assertThat(frequencias.get(1).isAbaixoDoMinimo()).isTrue();
    }
}
