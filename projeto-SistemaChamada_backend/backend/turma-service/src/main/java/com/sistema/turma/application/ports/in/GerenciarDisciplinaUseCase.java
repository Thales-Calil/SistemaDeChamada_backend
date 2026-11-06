package com.sistema.turma.application.ports.in;

import com.sistema.turma.domain.model.Disciplina;
import java.util.List;

public interface GerenciarDisciplinaUseCase {
    List<Disciplina> listarTodos();
    Disciplina buscarPorId(Long id);
    Disciplina cadastrar(Disciplina disciplina);
    Disciplina atualizar(Long id, Disciplina disciplina);
    void excluir(Long id);
}