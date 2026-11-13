package com.sistema.chamada.infrastructure.adapters.out.persistence.repository;

import com.sistema.chamada.infrastructure.adapters.out.persistence.entity.ChamadaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SpringDataChamadaRepository extends JpaRepository<ChamadaEntity, Long> {
    List<ChamadaEntity> findByTurmaIdOrderByDataDescHorarioDesc(Long turmaId);
    boolean existsByTurmaIdAndDataAndHorario(Long turmaId, LocalDate data, LocalTime horario);
}
