package com.sistema.turma.application.usecase.service;

import com.sistema.turma.application.ports.in.GerenciarProfessorUseCase;
import com.sistema.turma.application.ports.out.ProfessorRepositoryPort;
import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.domain.model.Professor;
import com.sistema.turma.application.exception.AcessoNegadoException;
import com.sistema.turma.application.exception.NaoEncontradoException;
import com.sistema.turma.application.exception.RegraNegocioException;
import com.sistema.turma.application.ports.out.UsuarioLogadoPort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessorService implements GerenciarProfessorUseCase {

    private final ProfessorRepositoryPort professorRepositoryPort;
    private final TurmaRepositoryPort turmaRepositoryPort;
    private final UsuarioLogadoPort usuarioLogadoPort;

    /** Leitura: ADMIN e PROFESSOR. Escrita: somente ADMIN. */
    private void exigirLeitura() {
        if (usuarioLogadoPort.atual().isAluno()) {
            throw new AcessoNegadoException("Aluno não tem acesso ao cadastro de professores.");
        }
    }

    private void exigirAdmin() {
        if (!usuarioLogadoPort.atual().isAdmin()) {
            throw new AcessoNegadoException("Somente ADMIN pode cadastrar, alterar ou excluir professores.");
        }
    }

    @Override
    public List<Professor> listarTodos() {
        exigirLeitura();
        return professorRepositoryPort.buscarTodos();
    }

    @Override
    public Professor buscarPorId(Long id) {
        exigirLeitura();
        return professorRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Professor " + id + " não encontrado."));
    }

    @Override
    public Professor cadastrar(Professor professor) {
        exigirAdmin();
        return professorRepositoryPort.salvar(professor);
    }

    @Override
    public Professor atualizar(Long id, Professor professorAtualizado) {
        exigirAdmin();
        Professor professorExistente = buscarPorId(id);

        professorExistente.setNome(professorAtualizado.getNome());
        professorExistente.setEmail(professorAtualizado.getEmail());

        return professorRepositoryPort.salvar(professorExistente);
    }

    @Override
    public void excluir(Long id) {
        exigirAdmin();
        if (!professorRepositoryPort.existePorId(id)) {
            throw new NaoEncontradoException("Professor " + id + " não encontrado.");
        }

        // Regra de negócio: não excluir se estiver vinculado a uma turma
        if (turmaRepositoryPort.existePorProfessorId(id)) {
            throw new RegraNegocioException("Não é possível excluir o professor porque ele está vinculado a uma turma");
        }

        professorRepositoryPort.excluir(id);
    }
}