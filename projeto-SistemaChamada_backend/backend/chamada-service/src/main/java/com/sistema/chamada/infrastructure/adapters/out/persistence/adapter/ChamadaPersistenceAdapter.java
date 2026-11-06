package com.sistema.chamada.infrastructure.adapters.out.persistence.adapter;

import com.sistema.chamada.application.ports.out.ChamadaRepositoryPort;
import com.sistema.chamada.domain.model.Chamada;
import com.sistema.chamada.infrastructure.adapters.out.persistence.entity.ChamadaEntity;
import com.sistema.chamada.infrastructure.adapters.out.persistence.repository.SpringDataChamadaRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ChamadaPersistenceAdapter implements ChamadaRepositoryPort {

    private final SpringDataChamadaRepository repository;

    @Override
    public Chamada salvar(Chamada chamada) {
        ChamadaEntity entity = toEntity(chamada);
        return toDomain(repository.save(entity));
    }

    @Override
    public Optional<Chamada> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Chamada> listarPorTurma(Long turmaId) {
        return repository.findByTurmaId(turmaId).stream().map(this::toDomain).toList();
    }

    @Override
    public void excluir(Long id) {
        repository.deleteById(id);
    }

    private Chamada toDomain(ChamadaEntity entity) {
        Chamada chamada = new Chamada();
        chamada.setId(entity.getId());
        chamada.setTurmaId(entity.getTurmaId());
        chamada.setData(entity.getData());
        chamada.setHorario(entity.getHorario());
        chamada.setStatus(entity.getStatus());
        return chamada;
    }

    private ChamadaEntity toEntity(Chamada chamada) {
        ChamadaEntity entity = new ChamadaEntity();
        entity.setId(chamada.getId());
        entity.setTurmaId(chamada.getTurmaId());
        entity.setData(chamada.getData());
        entity.setHorario(chamada.getHorario());
        entity.setStatus(chamada.getStatus());
        return entity;
    }
}