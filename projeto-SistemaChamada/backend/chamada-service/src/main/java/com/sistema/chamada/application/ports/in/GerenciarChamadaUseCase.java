package com.sistema.chamada.application.ports.in;

import com.sistema.chamada.domain.model.Chamada;
import com.sistema.chamada.domain.model.Presenca;
import com.sistema.chamada.infrastructure.adapters.in.web.dto.PresencaTurmaDTO;
import java.util.List;

public interface GerenciarChamadaUseCase {
    Chamada criar(Chamada chamada);
    Chamada buscarPorId(Long id);
    List<Chamada> listarPorTurma(Long turmaId);
    void finalizar(Long id);
    void excluir(Long id);
}