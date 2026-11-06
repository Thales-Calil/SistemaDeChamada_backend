package com.sistema.chamada.application.ports.out;

import com.sistema.chamada.domain.model.Presenca;
import java.util.List;
import java.util.Optional;

public interface PresencaRepositoryPort {
    Presenca salvar(Presenca presenca);
    Optional<Presenca> buscarPorChamadaEAluno(Long chamadaId, Long alunoId);
    List<Presenca> buscarPorChamadas(List<Long> chamadaIds);
}