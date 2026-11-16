package com.sistema.turma.application.ports.in;

import com.sistema.turma.domain.model.Chamada;
import java.util.List;

public interface GerenciarChamadaUseCase {
    Chamada criar(Chamada chamada);
    Chamada buscarPorId(Long id);
    List<Chamada> listarPorTurma(Long turmaId);
    void finalizar(Long id);
    void excluir(Long id);
}
