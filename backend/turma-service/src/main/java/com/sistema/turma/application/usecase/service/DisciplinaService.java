package com.sistema.turma.application.usecase.service;

import com.sistema.turma.application.ports.in.GerenciarDisciplinaUseCase;
import com.sistema.turma.application.ports.out.DisciplinaRepositoryPort;
import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.domain.model.Disciplina;
import com.sistema.turma.application.exception.AcessoNegadoException;
import com.sistema.turma.application.exception.NaoEncontradoException;
import com.sistema.turma.application.exception.RegraNegocioException;
import com.sistema.turma.application.ports.out.UsuarioLogadoPort;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DisciplinaService implements GerenciarDisciplinaUseCase {

    private final DisciplinaRepositoryPort disciplinaRepositoryPort;
    private final TurmaRepositoryPort turmaRepositoryPort;
    private final UsuarioLogadoPort usuarioLogadoPort;

    /** Leitura: ADMIN e PROFESSOR. Escrita: somente ADMIN. */
    private void exigirLeitura() {
        if (usuarioLogadoPort.atual().isAluno()) {
            throw new AcessoNegadoException("Aluno não tem acesso ao cadastro de disciplinas.");
        }
    }

    private void exigirAdmin() {
        if (!usuarioLogadoPort.atual().isAdmin()) {
            throw new AcessoNegadoException("Somente ADMIN pode cadastrar, alterar ou excluir disciplinas.");
        }
    }

    @Override
    public List<Disciplina> listarTodos() {
        exigirLeitura();
        return disciplinaRepositoryPort.buscarTodos();
    }

    @Override
    public Disciplina buscarPorId(Long id) {
        exigirLeitura();
        return disciplinaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Disciplina " + id + " não encontrada."));
    }

    @Override
    public Disciplina cadastrar(Disciplina disciplina) {
        exigirAdmin();
        return disciplinaRepositoryPort.salvar(disciplina);
    }

    @Override
    public Disciplina atualizar(Long id, Disciplina disciplinaAtualizada) {
        exigirAdmin();
        Disciplina disciplinaExistente = buscarPorId(id);

        disciplinaExistente.setNome(disciplinaAtualizada.getNome());
        disciplinaExistente.setCodigo(disciplinaAtualizada.getCodigo());

        return disciplinaRepositoryPort.salvar(disciplinaExistente);
    }

    @Override
    public void excluir(Long id) {
        exigirAdmin();
        if (!disciplinaRepositoryPort.existePorId(id)) {
            throw new NaoEncontradoException("Disciplina " + id + " não encontrada.");
        }

        // Regra de negócio: não excluir se estiver vinculada a uma turma
        if (turmaRepositoryPort.existePorDisciplinaId(id)) {
            throw new RegraNegocioException("Não é possível excluir a disciplina porque ela está vinculada a uma turma");
        }

        disciplinaRepositoryPort.excluir(id);
    }
}