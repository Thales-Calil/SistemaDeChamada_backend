package com.sistema.aluno.application.ports.out;

import com.sistema.aluno.domain.model.Aluno;
import java.util.List;
import java.util.Optional;

public interface AlunoRepositoryPort {
    List<Aluno> buscarTodos();
    Optional<Aluno> buscarPorId(String id);
    Aluno salvar(Aluno aluno);
    void excluir(String id);
    boolean existePorId(String id);
}