package com.sistema.chamada.application.ports.in;

import com.sistema.chamada.domain.model.Presenca;
import com.sistema.chamada.infrastructure.adapters.in.web.dto.PresencaTurmaDTO;
import java.util.List;

public interface GerenciarPresencaUseCase {
    Presenca registrarPresenca(Long chamadaId, Long alunoId, Boolean presente);
    Presenca atualizarPresenca(Long chamadaId, Long alunoId, Boolean presente);
    List<PresencaTurmaDTO> calcularPorTurma(Long turmaId);
}