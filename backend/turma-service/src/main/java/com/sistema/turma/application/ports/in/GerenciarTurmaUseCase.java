package com.sistema.turma.application.ports.in;

import com.sistema.turma.domain.model.Turma;
import java.util.List;

public interface GerenciarTurmaUseCase {
    /** ADMIN: todas | PROFESSOR: as suas | ALUNO: aquelas em que está matriculado. */
    List<Turma> listarTodos();
    Turma buscarPorId(Long id);
    Turma cadastrar(Turma turma);
    Turma atualizar(Long id, Turma turma);
    void excluir(Long id);
}
