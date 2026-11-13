package com.sistema.aluno.application.ports.in;

import com.sistema.aluno.domain.model.Aluno;
import java.util.List;

public interface GerenciarAlunoUseCase {
    /**
     * ADMIN: todos | PROFESSOR: alunos das suas turmas | ALUNO: só ele mesmo.
     * @param ids filtro opcional (ex.: alunos de uma turma); nulo = sem filtro
     */
    List<Aluno> listar(List<Long> ids);
    Aluno buscarPorId(Long id);
    Aluno cadastrar(Aluno aluno);
    Aluno atualizar(Long id, Aluno aluno);
    void excluir(Long id);
}
