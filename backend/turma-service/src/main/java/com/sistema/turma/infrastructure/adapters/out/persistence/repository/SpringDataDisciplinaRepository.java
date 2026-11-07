package com.sistema.turma.infrastructure.adapters.out.persistence.repository;

import com.sistema.turma.infrastructure.adapters.out.persistence.entity.DisciplinaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataDisciplinaRepository extends JpaRepository<DisciplinaEntity, Long> {

}