package com.sistema.turma.application.usecase;

import com.sistema.turma.application.exception.AcessoNegadoException;
import com.sistema.turma.application.exception.NaoEncontradoException;
import com.sistema.turma.application.exception.RegraNegocioException;
import com.sistema.turma.application.ports.out.AlunoServicePort;
import com.sistema.turma.application.ports.out.MatriculaRepositoryPort;
import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.application.ports.out.UsuarioLogadoPort;
import com.sistema.turma.application.security.Perfil;
import com.sistema.turma.application.security.UsuarioLogado;
import com.sistema.turma.application.usecase.service.MatriculaService;
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
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatriculaServiceTest {

    @Mock private TurmaRepositoryPort turmaRepositoryPort;
    @Mock private MatriculaRepositoryPort matriculaRepositoryPort;
    @Mock private AlunoServicePort alunoServicePort;
    @Mock private UsuarioLogadoPort usuarioLogadoPort;

    @InjectMocks
    private MatriculaService matriculaService;

    private static final UsuarioLogado ADMIN = new UsuarioLogado(1L, "admin", Perfil.ADMIN, null);
    private static final UsuarioLogado PROFESSOR_5 = new UsuarioLogado(2L, "prof", Perfil.PROFESSOR, 5L);
    private static final UsuarioLogado ALUNO_42 = new UsuarioLogado(4L, "aluno", Perfil.ALUNO, 42L);

    private Turma turma;

    @BeforeEach
    void setUp() {
        Professor professor = new Professor();
        professor.setId(5L);
        turma = new Turma();
        turma.setId(1L);
        turma.setProfessor(professor);
    }

    @Test
    void adminMatriculaAlunoExistente() {
        when(usuarioLogadoPort.atual()).thenReturn(ADMIN);
        when(turmaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(turma));
        when(matriculaRepositoryPort.existe(1L, 42L)).thenReturn(false);
        when(alunoServicePort.existeAluno(42L)).thenReturn(true);

        matriculaService.matricular(1L, 42L);

        verify(matriculaRepositoryPort).salvar(1L, 42L);
    }

    @Test
    void naoMatriculaAlunoInexistente() {
        when(usuarioLogadoPort.atual()).thenReturn(ADMIN);
        when(turmaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(turma));
        when(matriculaRepositoryPort.existe(1L, 77L)).thenReturn(false);
        when(alunoServicePort.existeAluno(77L)).thenReturn(false);

        assertThatThrownBy(() -> matriculaService.matricular(1L, 77L)).isInstanceOf(NaoEncontradoException.class);
        verify(matriculaRepositoryPort, never()).salvar(anyLong(), anyLong());
    }

    @Test
    void naoMatriculaDuasVezes() {
        when(usuarioLogadoPort.atual()).thenReturn(ADMIN);
        when(turmaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(turma));
        when(matriculaRepositoryPort.existe(1L, 42L)).thenReturn(true);

        assertThatThrownBy(() -> matriculaService.matricular(1L, 42L)).isInstanceOf(RegraNegocioException.class);
    }

    @Test
    void professorDaTurmaVeOsAlunos() {
        when(usuarioLogadoPort.atual()).thenReturn(PROFESSOR_5);
        when(turmaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(turma));
        when(matriculaRepositoryPort.listarAlunoIds(1L)).thenReturn(List.of(42L, 43L));

        assertThat(matriculaService.listarAlunosDaTurma(1L)).containsExactly(42L, 43L);
    }

    @Test
    void alunoNaoVeTurmasDeOutroAluno() {
        when(usuarioLogadoPort.atual()).thenReturn(ALUNO_42);
        assertThatThrownBy(() -> matriculaService.listarTurmasDoAluno(43L)).isInstanceOf(AcessoNegadoException.class);
    }
}
