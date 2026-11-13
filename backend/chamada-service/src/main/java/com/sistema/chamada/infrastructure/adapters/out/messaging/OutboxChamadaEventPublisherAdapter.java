package com.sistema.chamada.infrastructure.adapters.out.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.sistema.chamada.application.ports.out.ChamadaEventPublisherPort;
import com.sistema.chamada.domain.event.ChamadaFinalizadaEvent;
import com.sistema.chamada.infrastructure.adapters.out.persistence.entity.OutboxEventoEntity;
import com.sistema.chamada.infrastructure.adapters.out.persistence.repository.SpringDataOutboxRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Em vez de enviar direto ao Service Bus (o que podia falhar depois de a chamada já estar FINALIZADA),
 * grava o evento na tabela outbox_eventos dentro da MESMA transação. O OutboxRelay envia depois.
 */
@Component
@RequiredArgsConstructor
public class OutboxChamadaEventPublisherAdapter implements ChamadaEventPublisherPort {

    public static final String TIPO_CHAMADA_FINALIZADA = "CHAMADA_FINALIZADA";

    static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private final SpringDataOutboxRepository outboxRepository;

    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void publicarChamadaFinalizada(ChamadaFinalizadaEvent evento) {
        String chave = TIPO_CHAMADA_FINALIZADA + "-" + evento.getChamadaId();
        if (outboxRepository.existsByChave(chave)) {
            return; // já registrado: não duplica
        }
        OutboxEventoEntity registro = new OutboxEventoEntity();
        registro.setTipo(TIPO_CHAMADA_FINALIZADA);
        registro.setChave(chave);
        registro.setPayload(serializar(evento));
        registro.setCriadoEm(LocalDateTime.now());
        registro.setTentativas(0);
        outboxRepository.save(registro);
    }

    private static String serializar(ChamadaFinalizadaEvent evento) {
        try {
            return OBJECT_MAPPER.writeValueAsString(evento);
        } catch (Exception e) {
            throw new IllegalStateException("Falha ao serializar o evento ChamadaFinalizada", e);
        }
    }
}
