package com.sistema.turma.infrastructure.adapters.out.persistence.adapter;

import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.domain.model.Disciplina;
import com.sistema.turma.domain.model.Professor;
import com.sistema.turma.domain.model.Turma;
import com.sistema.turma.infrastructure.adapters.out.persistence.entity.TurmaEntity;
import com.sistema.turma.infrastructure.adapters.out.persistence.repository.SpringDataDisciplinaRepository;
import com.sistema.turma.infrastructure.adapters.out.persistence.repository.SpringDataProfessorRepository;
import com.sistema.turma.infrastructure.adapters.out.persistence.repository.SpringDataTurmaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TurmaPersistenceAdapter implements TurmaRepositoryPort {

    private final SpringDataTurmaRepository repository;
    private final SpringDataProfessorRepository professorRepository;
    private final SpringDataDisciplinaRepository disciplinaRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Turma> buscarTodos() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Turma> buscarPorIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return repository.findAllById(ids).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Turma> buscarPorProfessorId(Long professorId) {
        return repository.findByProfessorId(professorId).stream().map(this::toDomain).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Turma> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    @Transactional
    public Turma salvar(Turma turma) {
        TurmaEntity savedEntity = repository.save(toEntity(turma));
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

    @Override
    @Transactional
    public void incrementarAulasMinistradas(Long turmaId) {
        repository.incrementarAulasMinistradas(turmaId);
    }

    // Mapeamento Entity -> Domain (Lida com os relacionamentos)
    private Turma toDomain(TurmaEntity entity) {
        Turma turma = new Turma();
        turma.setId(entity.getId());
        turma.setNome(entity.getNome());
        turma.setAulasMinistradas(entity.getAulasMinistradas());

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

    // Mapeamento Domain -> Entity (usa referências gerenciadas para o nome do professor/disciplina vir preenchido)
    private TurmaEntity toEntity(Turma turma) {
        TurmaEntity entity = new TurmaEntity();
        entity.setId(turma.getId());
        entity.setNome(turma.getNome());
        entity.setAulasMinistradas(turma.getAulasMinistradas());
        entity.setProfessor(professorRepository.getReferenceById(turma.getProfessor().getId()));
        entity.setDisciplina(disciplinaRepository.getReferenceById(turma.getDisciplina().getId()));
        return entity;
    }
}
