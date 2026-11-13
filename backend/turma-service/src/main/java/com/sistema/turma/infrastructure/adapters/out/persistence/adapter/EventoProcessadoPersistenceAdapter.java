package com.sistema.turma.infrastructure.adapters.out.persistence.adapter;

import com.sistema.turma.application.ports.out.EventoProcessadoPort;
import com.sistema.turma.infrastructure.adapters.out.persistence.entity.EventoProcessadoEntity;
import com.sistema.turma.infrastructure.adapters.out.persistence.repository.SpringDataEventoProcessadoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class EventoProcessadoPersistenceAdapter implements EventoProcessadoPort {

    private final SpringDataEventoProcessadoRepository repository;

    @Override
    public boolean registrarSeNovo(String chave) {
        if (repository.existsById(chave)) {
            return false;
        }
        // Se duas entregas simultâneas chegarem aqui, a PK impede a segunda (ela é reprocessada e então ignorada)
        repository.saveAndFlush(new EventoProcessadoEntity(chave, LocalDateTime.now()));
        return true;
    }
}
