package com.sistema.turma.infrastructure.adapters.out.persistence.repository;

import com.sistema.turma.infrastructure.adapters.out.persistence.entity.MatriculaEntity;
import com.sistema.turma.infrastructure.adapters.out.persistence.entity.MatriculaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface SpringDataMatriculaRepository extends JpaRepository<MatriculaEntity, MatriculaId> {
    List<MatriculaEntity> findByTurmaId(Long turmaId);
    List<MatriculaEntity> findByAlunoId(Long alunoId);
    List<MatriculaEntity> findByTurmaIdIn(Collection<Long> turmaIds);
    boolean existsByTurmaIdAndAlunoId(Long turmaId, Long alunoId);

    @Modifying
    @Query("delete from turma_alunos m where m.turmaId = :turmaId and m.alunoId = :alunoId")
    int excluir(@Param("turmaId") Long turmaId, @Param("alunoId") Long alunoId);

    @Modifying
    @Query("delete from turma_alunos m where m.turmaId = :turmaId")
    int excluirPorTurma(@Param("turmaId") Long turmaId);
}
