package com.sistema.turma.infrastructure.adapters.out.persistence.repository;

import com.sistema.turma.infrastructure.adapters.out.persistence.entity.EventoProcessadoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataEventoProcessadoRepository extends JpaRepository<EventoProcessadoEntity, String> {
}
