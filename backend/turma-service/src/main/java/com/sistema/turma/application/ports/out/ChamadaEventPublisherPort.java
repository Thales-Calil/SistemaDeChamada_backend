package com.sistema.turma.application.ports.out;

import com.sistema.turma.domain.event.ChamadaFinalizadaEvent;

public interface ChamadaEventPublisherPort {
    void publicarChamadaFinalizada(ChamadaFinalizadaEvent evento);
}