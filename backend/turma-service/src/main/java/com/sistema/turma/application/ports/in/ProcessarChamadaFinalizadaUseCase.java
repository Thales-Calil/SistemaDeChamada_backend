package com.sistema.turma.application.ports.in;

import com.sistema.turma.domain.event.ChamadaFinalizadaEvent;

public interface ProcessarChamadaFinalizadaUseCase {
    void processar(ChamadaFinalizadaEvent evento);
}