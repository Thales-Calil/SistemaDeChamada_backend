package com.sistema.turma.application.usecase;

import com.sistema.turma.application.exception.AcessoNegadoException;
import com.sistema.turma.application.exception.NaoEncontradoException;
import com.sistema.turma.application.exception.RegraNegocioException;
import com.sistema.turma.application.ports.out.*;
import com.sistema.turma.application.security.Perfil;
import com.sistema.turma.application.security.UsuarioLogado;
import com.sistema.turma.application.usecase.service.TurmaService;
import com.sistema.turma.domain.model.Disciplina;
import com.sistema.turma.domain.model.Professor;
import com.sistema.turma.domain.model.Turma;
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
class TurmaServiceTest {

    @Mock private TurmaRepositoryPort turmaRepositoryPort;
    @Mock private ProfessorRepositoryPort professorRepositoryPort;
    @Mock private DisciplinaRepositoryPort disciplinaRepositoryPort;
    @Mock private MatriculaRepositoryPort matriculaRepositoryPort;
    @Mock private ChamadaServicePort chamadaServicePort;
    @Mock private UsuarioLogadoPort usuarioLogadoPort;

    @InjectMocks
    private TurmaService turmaService;

    private static final UsuarioLogado ADMIN = new UsuarioLogado(1L, "admin", Perfil.ADMIN, null);
    private static final UsuarioLogado PROFESSOR_5 = new UsuarioLogado(2L, "prof", Perfil.PROFESSOR, 5L);
    private static final UsuarioLogado PROFESSOR_9 = new UsuarioLogado(3L, "outro", Perfil.PROFESSOR, 9L);
    private static final UsuarioLogado ALUNO_42 = new UsuarioLogado(4L, "aluno", Perfil.ALUNO, 42L);

    private Turma turma;
    private Professor professor;
    private Disciplina disciplina;

    @BeforeEach
    void setUp() {
        professor = new Professor();
        professor.setId(5L);
        disciplina = new Disciplina();
        disciplina.setId(10L);
        turma = new Turma();
        turma.setId(1L);
        turma.setNome("Arquitetura de Soluções");
        turma.setProfessor(professor);
        turma.setDisciplina(disciplina);
    }

    @Test
    void adminCadastraTurma() {
        when(usuarioLogadoPort.atual()).thenReturn(ADMIN);
        when(professorRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(professor));
        when(disciplinaRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(disciplina));
        when(turmaRepositoryPort.salvar(any(Turma.class))).thenReturn(turma);

        Turma resultado = turmaService.cadastrar(turma);

        assertThat(resultado.getNome()).isEqualTo("Arquitetura de Soluções");
        verify(turmaRepositoryPort).salvar(turma);
    }

    @Test
    void professorNaoCadastraTurma() {
        when(usuarioLogadoPort.atual()).thenReturn(PROFESSOR_5);
        assertThatThrownBy(() -> turmaService.cadastrar(turma)).isInstanceOf(AcessoNegadoException.class);
        verify(turmaRepositoryPort, never()).salvar(any());
    }

    @Test
    void professorVeSomenteSuasTurmas() {
        when(usuarioLogadoPort.atual()).thenReturn(PROFESSOR_5);
        when(turmaRepositoryPort.buscarPorProfessorId(5L)).thenReturn(List.of(turma));

        assertThat(turmaService.listarTodos()).containsExactly(turma);
        verify(turmaRepositoryPort, never()).buscarTodos();
    }

    @Test
    void professorDeOutraTurmaRecebe403() {
        when(usuarioLogadoPort.atual()).thenReturn(PROFESSOR_9);
        when(turmaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(turma));

        assertThatThrownBy(() -> turmaService.buscarPorId(1L)).isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void alunoMatriculadoVeATurma() {
        when(usuarioLogadoPort.atual()).thenReturn(ALUNO_42);
        when(turmaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(turma));
        when(matriculaRepositoryPort.existe(1L, 42L)).thenReturn(true);

        assertThat(turmaService.buscarPorId(1L)).isEqualTo(turma);
    }

    @Test
    void turmaInexistenteRetorna404() {
        when(turmaRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> turmaService.buscarPorId(99L)).isInstanceOf(NaoEncontradoException.class);
    }

    @Test
    void naoExcluiTurmaComChamadas() {
        when(usuarioLogadoPort.atual()).thenReturn(ADMIN);
        when(turmaRepositoryPort.existePorId(1L)).thenReturn(true);
        when(chamadaServicePort.turmaPossuiChamadas(1L)).thenReturn(true);

        assertThatThrownBy(() -> turmaService.excluir(1L)).isInstanceOf(RegraNegocioException.class);
        verify(turmaRepositoryPort, never()).excluir(any());
    }
}
