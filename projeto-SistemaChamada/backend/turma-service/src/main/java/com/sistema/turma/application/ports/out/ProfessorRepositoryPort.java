package com.sistema.turma.application.ports.out;

import com.sistema.turma.domain.model.Professor;
import java.util.List;
import java.util.Optional;

public interface ProfessorRepositoryPort {
    List<Professor> buscarTodos();
    Optional<Professor> buscarPorId(Long id);
    Professor salvar(Professor professor);
    void excluir(Long id);
    boolean existePorId(Long id);
}
