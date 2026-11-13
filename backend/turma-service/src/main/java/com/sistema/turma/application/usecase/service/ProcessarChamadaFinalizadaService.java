package com.sistema.turma.application.usecase.service;

import com.sistema.turma.application.ports.in.ProcessarChamadaFinalizadaUseCase;
import com.sistema.turma.application.ports.out.EventoProcessadoPort;
import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.domain.event.ChamadaFinalizadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Consumidor do evento ChamadaFinalizada: soma 1 em "aulas ministradas" da turma.
 * Idempotente (2.4): o mesmo evento entregue duas vezes conta uma vez só.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProcessarChamadaFinalizadaService implements ProcessarChamadaFinalizadaUseCase {

    private final EventoProcessadoPort eventoProcessadoPort;
    private final TurmaRepositoryPort turmaRepositoryPort;

    @Override
    @Transactional
    public void processar(ChamadaFinalizadaEvent evento) {
        String chave = "CHAMADA_FINALIZADA-" + evento.getChamadaId();
        if (!eventoProcessadoPort.registrarSeNovo(chave)) {
            log.info("Evento {} já processado anteriormente. Ignorando.", chave);
            return;
        }
        if (!turmaRepositoryPort.existePorId(evento.getTurmaId())) {
            log.warn("Evento {} referencia a turma {} que não existe mais. Registrado sem alteração.", chave, evento.getTurmaId());
            return;
        }
        turmaRepositoryPort.incrementarAulasMinistradas(evento.getTurmaId());
        log.info("Turma {}: aula ministrada contabilizada (chamada {}).", evento.getTurmaId(), evento.getChamadaId());
    }
}
