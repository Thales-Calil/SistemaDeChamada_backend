package com.sistema.aluno.application.ports.in;

import com.sistema.aluno.domain.model.Aluno;
import java.util.List;

public interface GerenciarAlunoUseCase {
    List<Aluno> listarTodos();
    Aluno buscarPorId(String id);
    Aluno cadastrar(Aluno aluno);
    Aluno atualizar(String id, Aluno aluno);
    void excluir(String id);
}