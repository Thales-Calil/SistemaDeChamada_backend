package com.sistema.chamada.application.usecase.service;

import com.sistema.chamada.application.exception.AcessoNegadoException;
import com.sistema.chamada.application.exception.NaoEncontradoException;
import com.sistema.chamada.application.exception.RegraNegocioException;
import com.sistema.chamada.application.ports.in.FrequenciaTurma;
import com.sistema.chamada.application.ports.in.GerenciarChamadaUseCase;
import com.sistema.chamada.application.ports.in.GerenciarPresencaUseCase;
import com.sistema.chamada.application.ports.in.ItemPresenca;
import com.sistema.chamada.application.ports.out.ChamadaRepositoryPort;
import com.sistema.chamada.application.ports.out.PresencaRepositoryPort;
import com.sistema.chamada.application.ports.out.TurmaInfo;
import com.sistema.chamada.application.ports.out.TurmaServicePort;
import com.sistema.chamada.application.ports.out.UsuarioLogadoPort;
import com.sistema.chamada.application.security.UsuarioLogado;
import com.sistema.chamada.domain.model.Chamada;
import com.sistema.chamada.domain.model.FrequenciaAluno;
import com.sistema.chamada.domain.model.Presenca;
import com.sistema.chamada.domain.model.StatusChamada;
import com.sistema.chamada.domain.service.CalculadorFrequenciaDomainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PresencaService implements GerenciarPresencaUseCase {

    private final ChamadaRepositoryPort chamadaRepositoryPort;
    private final PresencaRepositoryPort presencaRepositoryPort;
    private final TurmaServicePort turmaServicePort;
    private final UsuarioLogadoPort usuarioLogadoPort;
    private final AcessoTurma acessoTurma;
    private final GerenciarChamadaUseCase gerenciarChamadaUseCase;

    // Instancia o serviço de domínio puro
    private final CalculadorFrequenciaDomainService calculadorDomainService = new CalculadorFrequenciaDomainService();

    @Override
    @Transactional
    public Presenca registrarPresenca(Long chamadaId, Long alunoId, Boolean presente) {
        Chamada chamada = chamadaAbertaComAcesso(chamadaId);
        validarMatriculados(chamada.getTurmaId(), List.of(alunoId));
        return gravar(chamadaId, alunoId, presente);
    }

    @Override
    @Transactional
    public Presenca atualizarPresenca(Long chamadaId, Long alunoId, Boolean presente) {
        chamadaAbertaComAcesso(chamadaId);
        Presenca presenca = presencaRepositoryPort.buscarPorChamadaEAluno(chamadaId, alunoId)
                .orElseThrow(() -> new NaoEncontradoException("Presença do aluno " + alunoId + " não encontrada na chamada " + chamadaId + "."));
        presenca.setPresente(presente);
        return presencaRepositoryPort.salvar(presenca);
    }

    @Override
    @Transactional
    public List<Presenca> registrarEmLote(Long chamadaId, List<ItemPresenca> itens, boolean finalizar) {
        if (itens == null || itens.isEmpty()) {
            throw new IllegalArgumentException("Informe ao menos um aluno.");
        }
        Set<Long> ids = new HashSet<>();
        for (ItemPresenca item : itens) {
            if (item.alunoId() == null || item.presente() == null) {
                throw new IllegalArgumentException("Cada item precisa de alunoId e presente.");
            }
            if (!ids.add(item.alunoId())) {
                throw new IllegalArgumentException("Aluno " + item.alunoId() + " repetido na lista.");
            }
        }

        Chamada chamada = chamadaAbertaComAcesso(chamadaId);
        validarMatriculados(chamada.getTurmaId(), ids);

        List<Presenca> gravadas = itens.stream()
                .map(item -> gravar(chamadaId, item.alunoId(), item.presente()))
                .toList();

        if (finalizar) {
            gerenciarChamadaUseCase.finalizar(chamadaId); // mesma transação: tudo ou nada
        }
        return gravadas;
    }

    @Override
    public List<Presenca> listarPresencasDaChamada(Long chamadaId) {
        Chamada chamada = chamadaRepositoryPort.buscarPorId(chamadaId)
                .orElseThrow(() -> new NaoEncontradoException("Chamada " + chamadaId + " não encontrada."));
        acessoTurma.verificar(chamada.getTurmaId());
        return presencaRepositoryPort.buscarPorChamada(chamadaId);
    }

    /** Frequência de todos os alunos da turma (inclui quem nunca teve presença registrada = 100% faltas). */
    @Override
    public List<FrequenciaAluno> calcularPorTurma(Long turmaId) {
        acessoTurma.verificar(turmaId);

        List<Long> idsChamadas = chamadasFinalizadas(turmaId);
        List<Presenca> presencas = idsChamadas.isEmpty() ? List.of() : presencaRepositoryPort.buscarPorChamadas(idsChamadas);
        Map<Long, Long> presencasPorAluno = contarPresencas(presencas);

        // alunos matriculados hoje + alunos que já tiveram presença registrada (histórico)
        Set<Long> alunos = new TreeSet<>(turmaServicePort.alunosDaTurma(turmaId));
        presencas.forEach(p -> alunos.add(p.getAlunoId()));

        return alunos.stream()
                .map(alunoId -> calculadorDomainService.calcular(alunoId, idsChamadas.size(),
                        presencasPorAluno.getOrDefault(alunoId, 0L).intValue()))
                .toList();
    }

    /** Frequência do aluno em cada turma que ele cursa (ALUNO: só a própria). */
    @Override
    public List<FrequenciaTurma> calcularPorAluno(Long alunoId) {
        UsuarioLogado u = usuarioLogadoPort.atual();
        if (u.isAluno() && !alunoId.equals(u.referenciaId())) {
            throw new AcessoNegadoException("Aluno só pode consultar a própria frequência.");
        }
        // o turma-service já filtra: professor recebe só as turmas dele
        List<TurmaInfo> turmas = turmaServicePort.turmasDoAluno(alunoId);

        List<FrequenciaTurma> resultado = new ArrayList<>();
        for (TurmaInfo turma : turmas) {
            List<Long> idsChamadas = chamadasFinalizadas(turma.id());
            int presencasAluno = idsChamadas.isEmpty() ? 0 : (int) presencaRepositoryPort.buscarPorChamadas(idsChamadas).stream()
                    .filter(p -> alunoId.equals(p.getAlunoId()) && Boolean.TRUE.equals(p.getPresente()))
                    .count();
            FrequenciaAluno frequencia = calculadorDomainService.calcular(alunoId, idsChamadas.size(), presencasAluno);
            resultado.add(new FrequenciaTurma(turma.id(), turma.nome(), turma.disciplinaNome(), frequencia));
        }
        return resultado;
    }

    // ----------------------------------------------------------------------------------

    private Chamada chamadaAbertaComAcesso(Long chamadaId) {
        Chamada chamada = chamadaRepositoryPort.buscarPorId(chamadaId)
                .orElseThrow(() -> new NaoEncontradoException("Chamada " + chamadaId + " não encontrada."));
        acessoTurma.verificar(chamada.getTurmaId());
        if (chamada.getStatus() == StatusChamada.FINALIZADA) {
            throw new RegraNegocioException("A chamada " + chamadaId + " já foi finalizada e não pode ser alterada.");
        }
        return chamada;
    }

    /** 2.2: só aceita presença de quem está matriculado na turma da chamada. */
    private void validarMatriculados(Long turmaId, Collection<Long> alunoIds) {
        Set<Long> matriculados = new HashSet<>(turmaServicePort.alunosDaTurma(turmaId));
        List<Long> foraDaTurma = alunoIds.stream().filter(id -> !matriculados.contains(id)).sorted().toList();
        if (!foraDaTurma.isEmpty()) {
            throw new RegraNegocioException("Aluno(s) " + foraDaTurma + " não estão matriculados na turma " + turmaId + ".");
        }
    }

    private Presenca gravar(Long chamadaId, Long alunoId, Boolean presente) {
        Presenca presenca = presencaRepositoryPort.buscarPorChamadaEAluno(chamadaId, alunoId).orElseGet(Presenca::new);
        presenca.setChamadaId(chamadaId);
        presenca.setAlunoId(alunoId);
        presenca.setPresente(presente);
        return presencaRepositoryPort.salvar(presenca);
    }

    private List<Long> chamadasFinalizadas(Long turmaId) {
        return chamadaRepositoryPort.listarPorTurma(turmaId).stream()
                .filter(c -> c.getStatus() == StatusChamada.FINALIZADA)
                .map(Chamada::getId)
                .toList();
    }

    private static Map<Long, Long> contarPresencas(List<Presenca> presencas) {
        return presencas.stream()
                .filter(p -> Boolean.TRUE.equals(p.getPresente()))
                .collect(Collectors.groupingBy(Presenca::getAlunoId, Collectors.counting()));
    }
}
