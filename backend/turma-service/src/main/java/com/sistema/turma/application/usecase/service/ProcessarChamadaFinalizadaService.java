package com.sistema.turma.application.usecase.service;

import com.sistema.turma.application.ports.in.ProcessarChamadaFinalizadaUseCase;
import com.sistema.turma.domain.event.ChamadaFinalizadaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProcessarChamadaFinalizadaService implements ProcessarChamadaFinalizadaUseCase {

    @Override
    public void processar(ChamadaFinalizadaEvent evento) {
        log.info("Processando evento assíncrono de Chamada Finalizada no TurmaService. Turma ID: {}, Chamada ID: {}",
                evento.getTurmaId(), evento.getChamadaId());

        // Regra de negócio da turma (ex: atualizar métricas/contagem de aulas ministradas)
    }
}