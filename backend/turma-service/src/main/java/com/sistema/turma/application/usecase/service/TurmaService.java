package com.sistema.turma.application.usecase.service;

import com.sistema.turma.application.exception.AcessoNegadoException;
import com.sistema.turma.application.exception.NaoEncontradoException;
import com.sistema.turma.application.exception.RegraNegocioException;
import com.sistema.turma.application.ports.in.GerenciarTurmaUseCase;
import com.sistema.turma.application.ports.out.ChamadaServicePort;
import com.sistema.turma.application.ports.out.DisciplinaRepositoryPort;
import com.sistema.turma.application.ports.out.MatriculaRepositoryPort;
import com.sistema.turma.application.ports.out.ProfessorRepositoryPort;
import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.application.ports.out.UsuarioLogadoPort;
import com.sistema.turma.application.security.UsuarioLogado;
import com.sistema.turma.domain.model.Disciplina;
import com.sistema.turma.domain.model.Professor;
import com.sistema.turma.domain.model.Turma;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TurmaService implements GerenciarTurmaUseCase {

    private final TurmaRepositoryPort turmaRepositoryPort;
    private final ProfessorRepositoryPort professorRepositoryPort;
    private final DisciplinaRepositoryPort disciplinaRepositoryPort;
    private final MatriculaRepositoryPort matriculaRepositoryPort;
    private final ChamadaServicePort chamadaServicePort;
    private final UsuarioLogadoPort usuarioLogadoPort;

    @Override
    public List<Turma> listarTodos() {
        UsuarioLogado u = usuarioLogadoPort.atual();
        if (u.isAdmin()) {
            return turmaRepositoryPort.buscarTodos();
        }
        if (u.isProfessor()) {
            return turmaRepositoryPort.buscarPorProfessorId(u.referenciaId());
        }
        return turmaRepositoryPort.buscarPorIds(matriculaRepositoryPort.listarTurmaIdsDoAluno(u.referenciaId()));
    }

    @Override
    public Turma buscarPorId(Long id) {
        Turma turma = buscarOuFalhar(id);
        verificarLeitura(turma, usuarioLogadoPort.atual());
        return turma;
    }

    @Override
    public Turma cadastrar(Turma turmaInfo) {
        exigirAdmin();
        turmaInfo.setProfessor(buscarProfessor(turmaInfo.getProfessor().getId()));
        turmaInfo.setDisciplina(buscarDisciplina(turmaInfo.getDisciplina().getId()));
        turmaInfo.setAulasMinistradas(0);
        return turmaRepositoryPort.salvar(turmaInfo);
    }

    @Override
    public Turma atualizar(Long id, Turma turmaInfo) {
        exigirAdmin();
        Turma turmaExistente = buscarOuFalhar(id);
        turmaExistente.setNome(turmaInfo.getNome());
        turmaExistente.setProfessor(buscarProfessor(turmaInfo.getProfessor().getId()));
        turmaExistente.setDisciplina(buscarDisciplina(turmaInfo.getDisciplina().getId()));
        return turmaRepositoryPort.salvar(turmaExistente);
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        exigirAdmin();
        if (!turmaRepositoryPort.existePorId(id)) {
            throw new NaoEncontradoException("Turma " + id + " não encontrada.");
        }
        // 2.7: integridade entre serviços - não deixa chamadas órfãs no chamada-service
        if (chamadaServicePort.turmaPossuiChamadas(id)) {
            throw new RegraNegocioException("A turma " + id + " possui chamadas registradas e não pode ser excluída.");
        }
        matriculaRepositoryPort.excluirPorTurma(id);
        turmaRepositoryPort.excluir(id);
    }

    // ----------------------------------------------------------------------------------

    Turma buscarOuFalhar(Long id) {
        return turmaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Turma " + id + " não encontrada."));
    }

    void verificarLeitura(Turma turma, UsuarioLogado u) {
        if (u.isAdmin()) return;
        if (u.isProfessor() && turma.getProfessor().getId().equals(u.referenciaId())) return;
        if (u.isAluno() && matriculaRepositoryPort.existe(turma.getId(), u.referenciaId())) return;
        throw new AcessoNegadoException("Você não tem acesso à turma " + turma.getId() + ".");
    }

    private void exigirAdmin() {
        if (!usuarioLogadoPort.atual().isAdmin()) {
            throw new AcessoNegadoException("Somente ADMIN pode cadastrar, alterar ou excluir turmas.");
        }
    }

    private Professor buscarProfessor(Long id) {
        return professorRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Professor " + id + " não encontrado."));
    }

    private Disciplina buscarDisciplina(Long id) {
        return disciplinaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Disciplina " + id + " não encontrada."));
    }
}
