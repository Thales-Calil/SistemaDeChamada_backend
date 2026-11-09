package com.sistema.aluno.application.ports.in;

import com.sistema.aluno.domain.event.ChamadaFinalizadaEvent;

public interface ProcessarChamadaFinalizadaUseCase {
    void processar(ChamadaFinalizadaEvent evento);
}