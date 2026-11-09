package com.sistema.turma.application.usecase.service;

import com.sistema.turma.application.ports.in.GerenciarProfessorUseCase;
import com.sistema.turma.application.ports.out.ProfessorRepositoryPort;
import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.domain.model.Professor;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfessorService implements GerenciarProfessorUseCase {

    private final ProfessorRepositoryPort professorRepositoryPort;
    private final TurmaRepositoryPort turmaRepositoryPort;

    @Override
    public List<Professor> listarTodos() {
        return professorRepositoryPort.buscarTodos();
    }

    @Override
    public Professor buscarPorId(Long id) {
        return professorRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));
    }

    @Override
    public Professor cadastrar(Professor professor) {
        return professorRepositoryPort.salvar(professor);
    }

    @Override
    public Professor atualizar(Long id, Professor professorAtualizado) {
        Professor professorExistente = buscarPorId(id);

        professorExistente.setNome(professorAtualizado.getNome());
        professorExistente.setEmail(professorAtualizado.getEmail());

        return professorRepositoryPort.salvar(professorExistente);
    }

    @Override
    public void excluir(Long id) {
        if (!professorRepositoryPort.existePorId(id)) {
            throw new RuntimeException("Professor não encontrado");
        }

        // Regra de negócio: não excluir se estiver vinculado a uma turma
        if (turmaRepositoryPort.existePorProfessorId(id)) {
            throw new RuntimeException("Não é possível excluir o professor porque ele está vinculado a uma turma");
        }

        professorRepositoryPort.excluir(id);
    }
}