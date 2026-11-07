package com.sistema.turma.infrastructure.adapters.out.persistence.repository;

import com.sistema.turma.infrastructure.adapters.out.persistence.entity.ProfessorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataProfessorRepository extends JpaRepository<ProfessorEntity, Long> {
}