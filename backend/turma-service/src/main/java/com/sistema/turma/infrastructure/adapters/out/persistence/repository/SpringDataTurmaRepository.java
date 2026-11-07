package com.sistema.turma.infrastructure.adapters.out.persistence.repository;

import com.sistema.turma.infrastructure.adapters.out.persistence.entity.TurmaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

public interface SpringDataTurmaRepository extends JpaRepository<TurmaEntity, Long> {
    boolean existsByProfessorId(Long professorId);
    boolean existsByDisciplinaId(Long disciplinaId);
}