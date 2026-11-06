package com.sistema.aluno.application.usecase;

import com.sistema.aluno.application.ports.in.GerenciarAlunoUseCase;
import com.sistema.aluno.application.ports.out.AlunoRepositoryPort;
import com.sistema.aluno.domain.model.Aluno;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AlunoService implements GerenciarAlunoUseCase {

    private final AlunoRepositoryPort alunoRepositoryPort;

    @Override
    public List<Aluno> listarTodos() {
        return alunoRepositoryPort.buscarTodos();
    }

    @Override
    public Aluno buscarPorId(String id) {
        return alunoRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));
    }

    @Override
    public Aluno cadastrar(Aluno aluno) {
        // Aqui entram as regras de negócio puras (ex: validar se matrícula já existe)
        return alunoRepositoryPort.salvar(aluno);
    }

    @Override
    public Aluno atualizar(String id, Aluno alunoAtualizado) {
        Aluno aluno = buscarPorId(id);

        aluno.setNome(alunoAtualizado.getNome());
        aluno.setMatricula(alunoAtualizado.getMatricula());
        aluno.setEmail(alunoAtualizado.getEmail());

        return alunoRepositoryPort.salvar(aluno);
    }

    @Override
    public void excluir(String id) {
        if (!alunoRepositoryPort.existePorId(id)) {
            throw new RuntimeException("Aluno não encontrado");
        }
        alunoRepositoryPort.excluir(id);
    }
}