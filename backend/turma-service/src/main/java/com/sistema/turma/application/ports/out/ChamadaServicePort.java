package com.sistema.turma.application.ports.out;

/** Consulta ao chamada-service (integridade entre serviços). */
public interface ChamadaServicePort {
    boolean turmaPossuiChamadas(Long turmaId);
}
