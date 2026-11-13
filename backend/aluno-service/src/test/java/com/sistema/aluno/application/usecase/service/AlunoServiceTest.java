package com.sistema.aluno.application.usecase.service;

import com.sistema.aluno.application.exception.AcessoNegadoException;
import com.sistema.aluno.application.exception.RegraNegocioException;
import com.sistema.aluno.application.ports.out.AlunoRepositoryPort;
import com.sistema.aluno.application.ports.out.TurmaServicePort;
import com.sistema.aluno.application.ports.out.UsuarioLogadoPort;
import com.sistema.aluno.application.security.Perfil;
import com.sistema.aluno.application.security.UsuarioLogado;
import com.sistema.aluno.domain.model.Aluno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock private AlunoRepositoryPort alunoRepositoryPort;
    @Mock private TurmaServicePort turmaServicePort;
    @Mock private UsuarioLogadoPort usuarioLogadoPort;

    @InjectMocks
    private AlunoService alunoService;

    private static final UsuarioLogado ADMIN = new UsuarioLogado(1L, "admin", Perfil.ADMIN, null);
    private static final UsuarioLogado PROFESSOR_5 = new UsuarioLogado(2L, "prof", Perfil.PROFESSOR, 5L);
    private static final UsuarioLogado ALUNO_42 = new UsuarioLogado(3L, "thales", Perfil.ALUNO, 42L);

    private Aluno aluno;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId(42L);
        aluno.setNome("Thales Calil");
        aluno.setMatricula("2026001");
        aluno.setEmail("thales@pucpr.edu.br");
    }

    @Test
    void adminCadastraAlunoComIdLongDaSequencia() {
        when(usuarioLogadoPort.atual()).thenReturn(ADMIN);
        when(alunoRepositoryPort.buscarPorMatricula("2026001")).thenReturn(Optional.empty());
        when(alunoRepositoryPort.buscarPorEmail("thales@pucpr.edu.br")).thenReturn(Optional.empty());
        when(alunoRepositoryPort.proximoId()).thenReturn(42L);
        when(alunoRepositoryPort.salvar(any(Aluno.class))).thenAnswer(inv -> inv.getArgument(0));

        Aluno novo = new Aluno();
        novo.setNome("Thales Calil");
        novo.setMatricula("2026001");
        novo.setEmail("thales@pucpr.edu.br");

        assertThat(alunoService.cadastrar(novo).getId()).isEqualTo(42L);
    }

    @Test
    void naoCadastraMatriculaDuplicada() {
        when(usuarioLogadoPort.atual()).thenReturn(ADMIN);
        when(alunoRepositoryPort.buscarPorMatricula("2026001")).thenReturn(Optional.of(aluno));

        Aluno novo = new Aluno();
        novo.setMatricula("2026001");
        novo.setEmail("outro@pucpr.edu.br");

        assertThatThrownBy(() -> alunoService.cadastrar(novo)).isInstanceOf(RegraNegocioException.class);
        verify(alunoRepositoryPort, never()).salvar(any());
    }

    @Test
    void alunoVeSomenteOsProprioDados() {
        when(usuarioLogadoPort.atual()).thenReturn(ALUNO_42);
        when(alunoRepositoryPort.buscarPorId(42L)).thenReturn(Optional.of(aluno));

        assertThat(alunoService.buscarPorId(42L).getMatricula()).isEqualTo("2026001");
        assertThatThrownBy(() -> alunoService.buscarPorId(43L)).isInstanceOf(AcessoNegadoException.class);
    }

    @Test
    void professorListaSomenteAlunosDasSuasTurmas() {
        when(usuarioLogadoPort.atual()).thenReturn(PROFESSOR_5);
        when(turmaServicePort.alunosDoProfessor(5L)).thenReturn(List.of(42L, 50L));
        when(alunoRepositoryPort.buscarPorIds(Set.of(42L))).thenReturn(List.of(aluno));

        // pediu 42 e 99, mas 99 não é aluno dele
        assertThat(alunoService.listar(List.of(42L, 99L))).containsExactly(aluno);
    }

    @Test
    void naoExcluiAlunoMatriculado() {
        when(usuarioLogadoPort.atual()).thenReturn(ADMIN);
        when(alunoRepositoryPort.existePorId(42L)).thenReturn(true);
        when(turmaServicePort.alunoPossuiTurmas(42L)).thenReturn(true);

        assertThatThrownBy(() -> alunoService.excluir(42L)).isInstanceOf(RegraNegocioException.class);
        verify(alunoRepositoryPort, never()).excluir(any());
    }
}
