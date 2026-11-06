package com.sistema.chamada.application.ports.out;

import com.sistema.chamada.domain.model.Chamada;
import java.util.List;
import java.util.Optional;

public interface ChamadaRepositoryPort {
    Chamada salvar(Chamada chamada);
    Optional<Chamada> buscarPorId(Long id);
    List<Chamada> listarPorTurma(Long turmaId);
    void excluir(Long id);
}