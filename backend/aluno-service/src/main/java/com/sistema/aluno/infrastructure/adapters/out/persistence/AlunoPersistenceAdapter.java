package com.sistema.aluno.infrastructure.adapters.out.persistence;

import com.sistema.aluno.application.ports.out.AlunoRepositoryPort;
import com.sistema.aluno.domain.model.Aluno;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AlunoPersistenceAdapter implements AlunoRepositoryPort {

    private final SpringDataAlunoRepository repository;
    private final SequenciaAlunoGenerator sequencia;

    @Override
    public List<Aluno> buscarTodos() {
        return repository.findAll().stream().map(this::toDomain).sorted(Comparator.comparing(Aluno::getNome, Comparator.nullsLast(String::compareToIgnoreCase))).toList();
    }

    @Override
    public List<Aluno> buscarPorIds(Collection<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return repository.findAllById(ids).stream().map(this::toDomain).sorted(Comparator.comparing(Aluno::getNome, Comparator.nullsLast(String::compareToIgnoreCase))).toList();
    }

    @Override
    public Optional<Aluno> buscarPorId(Long id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Aluno> buscarPorMatricula(String matricula) {
        return repository.findFirstByMatricula(matricula).map(this::toDomain);
    }

    @Override
    public Optional<Aluno> buscarPorEmail(String email) {
        return repository.findFirstByEmail(email).map(this::toDomain);
    }

    @Override
    public Aluno salvar(Aluno aluno) {
        return toDomain(repository.save(toEntity(aluno)));
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
    public Long proximoId() {
        return sequencia.proximo();
    }

    private Aluno toDomain(AlunoEntity entity) {
        Aluno aluno = new Aluno();
        aluno.setId(entity.getId());
        aluno.setNome(entity.getNome());
        aluno.setMatricula(entity.getMatricula());
        aluno.setEmail(entity.getEmail());
        return aluno;
    }

    private AlunoEntity toEntity(Aluno aluno) {
        AlunoEntity entity = new AlunoEntity();
        entity.setId(aluno.getId());
        entity.setNome(aluno.getNome());
        entity.setMatricula(aluno.getMatricula());
        entity.setEmail(aluno.getEmail());
        return entity;
    }
}
