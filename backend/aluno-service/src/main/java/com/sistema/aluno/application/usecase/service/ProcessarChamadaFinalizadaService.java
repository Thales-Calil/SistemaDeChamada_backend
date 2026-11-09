package com.sistema.aluno.application.usecase.service;

import com.sistema.aluno.application.ports.in.ProcessarChamadaFinalizadaUseCase;
import com.sistema.aluno.domain.event.ChamadaFinalizadaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProcessarChamadaFinalizadaService implements ProcessarChamadaFinalizadaUseCase {

    @Override
    public void processar(ChamadaFinalizadaEvent evento) {
        log.info("Processando evento assíncrono de Chamada Finalizada no AlunoService. Chamada ID: {}, Turma ID: {}",
                evento.getChamadaId(), evento.getTurmaId());

        // Aqui é executada a regra de negócio (ex: atualizar percentual de frequência do aluno)
    }
}