package com.sistema.chamada.infrastructure.adapters.out.persistence.repository;

import com.sistema.chamada.infrastructure.adapters.out.persistence.entity.ChamadaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SpringDataChamadaRepository extends JpaRepository<ChamadaEntity, Long> {
    List<ChamadaEntity> findByTurmaId(Long turmaId);
}