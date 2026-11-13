package com.sistema.turma.infrastructure.adapters.out.persistence.repository;

import com.sistema.turma.infrastructure.adapters.out.persistence.entity.TurmaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataTurmaRepository extends JpaRepository<TurmaEntity, Long> {
    boolean existsByProfessorId(Long professorId);
    boolean existsByDisciplinaId(Long disciplinaId);
    List<TurmaEntity> findByProfessorId(Long professorId);

    // "turmas" é o nome da entidade (@Entity(name = "turmas"))
    @Modifying
    @Query("update turmas t set t.aulasMinistradas = t.aulasMinistradas + 1 where t.id = :id")
    int incrementarAulasMinistradas(@Param("id") Long id);
}
