package com.sistema.turma.infrastructure.adapters.out.persistence.repository;

import com.sistema.turma.domain.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpringDataProfessorRepository extends JpaRepository<Professor, Long> {

}