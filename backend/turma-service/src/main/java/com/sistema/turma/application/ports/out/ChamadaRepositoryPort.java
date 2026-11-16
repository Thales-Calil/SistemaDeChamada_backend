package com.sistema.turma.application.ports.out;

import com.sistema.turma.domain.model.Chamada;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ChamadaRepositoryPort {
    Chamada salvar(Chamada chamada);
    Optional<Chamada> buscarPorId(Long id);
    List<Chamada> listarPorTurma(Long turmaId);
    boolean existe(Long turmaId, LocalDate data, LocalTime horario);
    boolean existePorTurma(Long turmaId);
    void excluir(Long id);
}
