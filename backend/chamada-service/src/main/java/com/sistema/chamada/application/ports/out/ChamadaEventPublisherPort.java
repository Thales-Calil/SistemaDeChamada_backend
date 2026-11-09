package com.sistema.chamada.application.ports.out;

import com.sistema.chamada.domain.event.ChamadaFinalizadaEvent;

public interface ChamadaEventPublisherPort {
    void publicarChamadaFinalizada(ChamadaFinalizadaEvent evento);
}