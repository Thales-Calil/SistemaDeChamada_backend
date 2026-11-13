package com.sistema.aluno.application.ports.out;

import com.sistema.aluno.domain.model.Aluno;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AlunoRepositoryPort {
    List<Aluno> buscarTodos();
    List<Aluno> buscarPorIds(Collection<Long> ids);
    Optional<Aluno> buscarPorId(Long id);
    Optional<Aluno> buscarPorMatricula(String matricula);
    Optional<Aluno> buscarPorEmail(String email);
    Aluno salvar(Aluno aluno);
    void excluir(Long id);
    boolean existePorId(Long id);
    /** Próximo id numérico da sequência de alunos. */
    Long proximoId();
}
