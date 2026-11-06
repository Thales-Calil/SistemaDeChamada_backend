package com.sistema.aluno.infrastructure.adapters.out.persistence;

import com.sistema.aluno.application.ports.out.AlunoRepositoryPort;
import com.sistema.aluno.domain.model.Aluno;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AlunoPersistenceAdapter implements AlunoRepositoryPort {

    private final SpringDataAlunoRepository repository;

    @Override
    public List<Aluno> buscarTodos() {
        return repository.findAll().stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Aluno> buscarPorId(String id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Aluno salvar(Aluno aluno) {
        AlunoEntity entity = toEntity(aluno);
        AlunoEntity savedEntity = repository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public void excluir(String id) {
        repository.deleteById(id);
    }

    @Override
    public boolean existePorId(String id) {
        return repository.existsById(id);
    }

    // Métodos utilitários de mapeamento (Podem ser extraídos para uma classe Mapper)
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