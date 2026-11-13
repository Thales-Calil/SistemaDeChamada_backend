package com.sistema.chamada.application.ports.in;

import com.sistema.chamada.domain.model.FrequenciaAluno;
import com.sistema.chamada.domain.model.Presenca;
import java.util.List;

public interface GerenciarPresencaUseCase {
    Presenca registrarPresenca(Long chamadaId, Long alunoId, Boolean presente);
    Presenca atualizarPresenca(Long chamadaId, Long alunoId, Boolean presente);
    /** 3.1: grava a chamada inteira numa transação só (tudo ou nada) e, se pedido, já finaliza. */
    List<Presenca> registrarEmLote(Long chamadaId, List<ItemPresenca> itens, boolean finalizar);
    List<Presenca> listarPresencasDaChamada(Long chamadaId);
    List<FrequenciaAluno> calcularPorTurma(Long turmaId);
    List<FrequenciaTurma> calcularPorAluno(Long alunoId);
}
