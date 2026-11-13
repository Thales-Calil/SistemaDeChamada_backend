package com.sistema.aluno.application.usecase.service;

import com.sistema.aluno.application.exception.AcessoNegadoException;
import com.sistema.aluno.application.exception.NaoEncontradoException;
import com.sistema.aluno.application.exception.RegraNegocioException;
import com.sistema.aluno.application.ports.in.GerenciarAlunoUseCase;
import com.sistema.aluno.application.ports.out.AlunoRepositoryPort;
import com.sistema.aluno.application.ports.out.TurmaServicePort;
import com.sistema.aluno.application.ports.out.UsuarioLogadoPort;
import com.sistema.aluno.application.security.UsuarioLogado;
import com.sistema.aluno.domain.model.Aluno;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AlunoService implements GerenciarAlunoUseCase {

    private final AlunoRepositoryPort alunoRepositoryPort;
    private final TurmaServicePort turmaServicePort;
    private final UsuarioLogadoPort usuarioLogadoPort;

    @Override
    public List<Aluno> listar(List<Long> ids) {
        UsuarioLogado u = usuarioLogadoPort.atual();

        if (u.isAdmin()) {
            return ids == null ? alunoRepositoryPort.buscarTodos() : alunoRepositoryPort.buscarPorIds(ids);
        }

        Set<Long> permitidos = u.isProfessor()
                ? new LinkedHashSet<>(turmaServicePort.alunosDoProfessor(u.referenciaId()))
                : new LinkedHashSet<>(List.of(u.referenciaId()));
        if (ids != null) {
            permitidos.retainAll(ids);
        }
        return permitidos.isEmpty() ? List.of() : alunoRepositoryPort.buscarPorIds(permitidos);
    }

    @Override
    public Aluno buscarPorId(Long id) {
        UsuarioLogado u = usuarioLogadoPort.atual();
        if (u.isAluno() && !id.equals(u.referenciaId())) {
            throw new AcessoNegadoException("Aluno só pode consultar os próprios dados.");
        }
        if (u.isProfessor() && !turmaServicePort.alunosDoProfessor(u.referenciaId()).contains(id)) {
            throw new AcessoNegadoException("O aluno " + id + " não pertence a nenhuma turma sua.");
        }
        return buscarOuFalhar(id);
    }

    @Override
    public Aluno cadastrar(Aluno aluno) {
        exigirAdmin();
        validarUnicidade(aluno, null);
        aluno.setId(alunoRepositoryPort.proximoId());
        return alunoRepositoryPort.salvar(aluno);
    }

    @Override
    public Aluno atualizar(Long id, Aluno alunoAtualizado) {
        exigirAdmin();
        Aluno aluno = buscarOuFalhar(id);
        validarUnicidade(alunoAtualizado, id);

        aluno.setNome(alunoAtualizado.getNome());
        aluno.setMatricula(alunoAtualizado.getMatricula());
        aluno.setEmail(alunoAtualizado.getEmail());

        return alunoRepositoryPort.salvar(aluno);
    }

    @Override
    public void excluir(Long id) {
        exigirAdmin();
        if (!alunoRepositoryPort.existePorId(id)) {
            throw new NaoEncontradoException("Aluno " + id + " não encontrado.");
        }
        // 2.7: não deixa matrícula órfã no turma-service
        if (turmaServicePort.alunoPossuiTurmas(id)) {
            throw new RegraNegocioException("O aluno " + id + " está matriculado em turmas. Remova-o das turmas antes de excluir.");
        }
        alunoRepositoryPort.excluir(id);
    }

    private Aluno buscarOuFalhar(Long id) {
        return alunoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Aluno " + id + " não encontrado."));
    }

    private void validarUnicidade(Aluno aluno, Long idAtual) {
        alunoRepositoryPort.buscarPorMatricula(aluno.getMatricula())
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(e -> {
                    throw new RegraNegocioException("Já existe aluno com a matrícula " + aluno.getMatricula() + ".");
                });
        alunoRepositoryPort.buscarPorEmail(aluno.getEmail())
                .filter(existente -> !existente.getId().equals(idAtual))
                .ifPresent(e -> {
                    throw new RegraNegocioException("Já existe aluno com o e-mail " + aluno.getEmail() + ".");
                });
    }

    private void exigirAdmin() {
        if (!usuarioLogadoPort.atual().isAdmin()) {
            throw new AcessoNegadoException("Somente ADMIN pode cadastrar, alterar ou excluir alunos.");
        }
    }
}
