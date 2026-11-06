package com.sistema.turma.infrastructure.adapters.out.persistence.adapter;

import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.domain.model.Disciplina;
import com.sistema.turma.domain.model.Professor;
import com.sistema.turma.domain.model.Turma;
import com.sistema.turma.infrastructure.adapters.out.persistence.entity.DisciplinaEntity;
import com.sistema.turma.infrastructure.adapters.out.persistence.entity.ProfessorEntity;
import com.sistema.turma.infrastructure.adapters.out.persistence.entity.TurmaEntity;
import com.sistema.turma.infrastructure.adapters.out.persistence.repository.SpringDataTurmaRepository;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TurmaPersistenceAdapter implements TurmaRepositoryPort {

    private final SpringDataTurmaRepository repository;

    @Override
    public List<Turma> buscarTodos() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Turma> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Turma salvar(Turma turma) {
        TurmaEntity entity = toEntity(turma);
        TurmaEntity savedEntity = repository.save(entity);
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

    @Override
    public boolean existePorProfessorId(Long professorId) {
        return repository.existsByProfessorId(professorId);
    }

    @Override
    public boolean existePorDisciplinaId(Long disciplinaId) {
        return repository.existsByDisciplinaId(disciplinaId);
    }

    // Mapeamento Entity -> Domain (Lida com os relacionamentos)
    private Turma toDomain(TurmaEntity entity) {
        Turma turma = new Turma();
        turma.setId(entity.getId());
        turma.setNome(entity.getNome());

        Professor professor = new Professor();
        professor.setId(entity.getProfessor().getId());
        professor.setNome(entity.getProfessor().getNome());
        professor.setEmail(entity.getProfessor().getEmail());
        turma.setProfessor(professor);

        Disciplina disciplina = new Disciplina();
        disciplina.setId(entity.getDisciplina().getId());
        disciplina.setNome(entity.getDisciplina().getNome());
        disciplina.setCodigo(entity.getDisciplina().getCodigo());
        turma.setDisciplina(disciplina);

        return turma;
    }

    // Mapeamento Domain -> Entity
    private TurmaEntity toEntity(Turma turma) {
        TurmaEntity entity = new TurmaEntity();
        entity.setId(turma.getId());
        entity.setNome(turma.getNome());

        ProfessorEntity professorEntity = new ProfessorEntity();
        professorEntity.setId(turma.getProfessor().getId());
        entity.setProfessor(professorEntity);

        DisciplinaEntity disciplinaEntity = new DisciplinaEntity();
        disciplinaEntity.setId(turma.getDisciplina().getId());
        entity.setDisciplina(disciplinaEntity);

        return entity;
    }
}