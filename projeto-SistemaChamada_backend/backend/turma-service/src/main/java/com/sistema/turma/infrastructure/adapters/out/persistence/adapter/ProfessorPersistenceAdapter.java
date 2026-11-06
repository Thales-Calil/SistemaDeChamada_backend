package com.sistema.turma.infrastructure.adapters.out.persistence.adapter;

import com.sistema.turma.application.ports.out.ProfessorRepositoryPort;
import com.sistema.turma.domain.model.Professor;
import com.sistema.turma.infrastructure.adapters.out.persistence.entity.ProfessorEntity;
import com.sistema.turma.infrastructure.adapters.out.persistence.repository.SpringDataProfessorRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ProfessorPersistenceAdapter implements ProfessorRepositoryPort {

    private final SpringDataProfessorRepository repository;

    @Override
    public List<Professor> buscarTodos() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Professor> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Professor salvar(Professor professor) {
        ProfessorEntity entity = toEntity(professor);
        ProfessorEntity savedEntity = repository.save(entity);
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

    // Mapeamento Entity -> Domain
    private Professor toDomain(ProfessorEntity entity) {
        Professor professor = new Professor();
        professor.setId(entity.getId());
        professor.setNome(entity.getNome());
        professor.setEmail(entity.getEmail());
        return professor;
    }

    // Mapeamento Domain -> Entity
    private ProfessorEntity toEntity(Professor professor) {
        ProfessorEntity entity = new ProfessorEntity();
        entity.setId(professor.getId());
        entity.setNome(professor.getNome());
        entity.setEmail(professor.getEmail());
        return entity;
    }
}