package com.sistema.turma.infrastructure.adapters.out.persistence.adapter;

import com.sistema.turma.application.ports.out.DisciplinaRepositoryPort;
import com.sistema.turma.domain.model.Disciplina;
import com.sistema.turma.infrastructure.adapters.out.persistence.entity.DisciplinaEntity;
import com.sistema.turma.infrastructure.adapters.out.persistence.repository.SpringDataDisciplinaRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Component // <-- É ISTO QUE RESOLVE O SEU ERRO!
@RequiredArgsConstructor
public class DisciplinaPersistenceAdapter implements DisciplinaRepositoryPort {

    private final SpringDataDisciplinaRepository repository;

    @Override
    public List<Disciplina> buscarTodos() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Disciplina> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Disciplina salvar(Disciplina disciplina) {
        DisciplinaEntity entity = toEntity(disciplina);
        DisciplinaEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public void excluir(Long id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existePorId(Long id) {
        return repository.existsById(id);
    }

    private Disciplina toDomain(DisciplinaEntity entity) {
        Disciplina disciplina = new Disciplina();
        disciplina.setId(entity.getId());
        disciplina.setNome(entity.getNome());
        disciplina.setCodigo(entity.getCodigo());
        return disciplina;
    }

    private DisciplinaEntity toEntity(Disciplina disciplina) {
        DisciplinaEntity entity = new DisciplinaEntity();
        entity.setId(disciplina.getId());
        entity.setNome(disciplina.getNome());
        entity.setCodigo(disciplina.getCodigo());
        return entity;
    }
}