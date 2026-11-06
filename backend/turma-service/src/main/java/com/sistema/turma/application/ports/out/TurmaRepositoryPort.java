package com.sistema.turma.application.ports.out;

import com.sistema.turma.domain.model.Turma;
import java.util.List;
import java.util.Optional;

public interface TurmaRepositoryPort {
    List<Turma> buscarTodos();
    Optional<Turma> buscarPorId(Long id);
    Turma salvar(Turma turma);
    void excluir(Long id);
    boolean existePorId(Long id);
    boolean existePorProfessorId(Long professorId);
    boolean existePorDisciplinaId(Long disciplinaId);
}