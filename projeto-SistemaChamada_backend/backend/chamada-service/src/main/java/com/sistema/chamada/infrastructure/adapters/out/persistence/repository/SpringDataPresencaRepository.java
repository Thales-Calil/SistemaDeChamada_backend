package com.sistema.chamada.infrastructure.adapters.out.persistence.repository;

import com.sistema.chamada.infrastructure.adapters.out.persistence.entity.PresencaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SpringDataPresencaRepository extends JpaRepository<PresencaEntity, Long> {
    Optional<PresencaEntity> findByChamadaIdAndAlunoId(Long chamadaId, Long alunoId);
    List<PresencaEntity> findByChamadaIdIn(List<Long> chamadaIds);
}