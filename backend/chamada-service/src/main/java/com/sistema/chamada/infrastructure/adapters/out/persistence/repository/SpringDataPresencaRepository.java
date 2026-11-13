package com.sistema.chamada.infrastructure.adapters.out.persistence.repository;

import com.sistema.chamada.infrastructure.adapters.out.persistence.entity.PresencaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataPresencaRepository extends JpaRepository<PresencaEntity, Long> {
    Optional<PresencaEntity> findByChamadaIdAndAlunoId(Long chamadaId, Long alunoId);
    List<PresencaEntity> findByChamadaId(Long chamadaId);
    List<PresencaEntity> findByChamadaIdIn(List<Long> chamadaIds);

    // "presencas" é o nome da entidade (@Entity(name = "presencas"))
    @Modifying
    @Query("delete from presencas p where p.chamadaId = :chamadaId")
    int excluirPorChamada(@Param("chamadaId") Long chamadaId);
}
