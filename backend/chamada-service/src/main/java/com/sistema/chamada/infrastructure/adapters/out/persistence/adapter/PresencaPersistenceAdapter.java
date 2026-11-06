package com.sistema.chamada.infrastructure.adapters.out.persistence.adapter;

import com.sistema.chamada.application.ports.out.PresencaRepositoryPort;
import com.sistema.chamada.domain.model.Presenca;
import com.sistema.chamada.infrastructure.adapters.out.persistence.entity.PresencaEntity;
import com.sistema.chamada.infrastructure.adapters.out.persistence.repository.SpringDataPresencaRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PresencaPersistenceAdapter implements PresencaRepositoryPort {

    private final SpringDataPresencaRepository repository;

    @Override
    public Presenca salvar(Presenca presenca) {
        PresencaEntity entity = toEntity(presenca);
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<Presenca> buscarPorChamadaEAluno(Long chamadaId, Long alunoId) {
        return repository.findByChamadaIdAndAlunoId(chamadaId, alunoId).map(this::toDomain);
    }

    @Override
    public List<Presenca> buscarPorChamadas(List<Long> chamadaIds) {
        return repository.findByChamadaIdIn(chamadaIds).stream().map(this::toDomain).toList();
    }

    private Presenca toDomain(PresencaEntity entity) {
        Presenca presenca = new Presenca();
        presenca.setId(entity.getId());
        presenca.setChamadaId(entity.getChamadaId());
        presenca.setAlunoId(entity.getAlunoId());
        presenca.setPresente(entity.getPresente());
        return presenca;
    }

    private PresencaEntity toEntity(Presenca presenca) {
        PresencaEntity entity = new PresencaEntity();
        entity.setId(presenca.getId());
        entity.setChamadaId(presenca.getChamadaId());
        entity.setAlunoId(presenca.getAlunoId());
        entity.setPresente(presenca.getPresente());
        return entity;
    }
}