package com.sistema.turma.application.ports.in;

import com.sistema.turma.domain.model.Professor;
import java.util.List;

public interface GerenciarProfessorUseCase {
    List<Professor> listarTodos();
    Professor buscarPorId(Long id);
    Professor cadastrar(Professor professor);
    Professor atualizar(Long id, Professor professor);
    void excluir(Long id);
}
