package com.sistema.chamada.infrastructure.adapters.out.persistence.repository;

import com.sistema.chamada.infrastructure.adapters.out.persistence.entity.OutboxEventoEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SpringDataOutboxRepository extends JpaRepository<OutboxEventoEntity, Long> {
    List<OutboxEventoEntity> findTop50ByEnviadoEmIsNullOrderByIdAsc();
    boolean existsByChave(String chave);
}
