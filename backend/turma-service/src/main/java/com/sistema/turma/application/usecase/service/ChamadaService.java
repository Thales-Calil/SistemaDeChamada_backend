package com.sistema.turma.application.usecase.service;

import com.sistema.turma.application.exception.NaoEncontradoException;
import com.sistema.turma.application.exception.RegraNegocioException;
import com.sistema.turma.application.ports.in.GerenciarChamadaUseCase;
import com.sistema.turma.application.ports.out.ChamadaEventPublisherPort;
import com.sistema.turma.application.ports.out.ChamadaRepositoryPort;
import com.sistema.turma.application.ports.out.PresencaRepositoryPort;
import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.domain.event.ChamadaFinalizadaEvent;
import com.sistema.turma.domain.model.Chamada;
import com.sistema.turma.domain.model.StatusChamada;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChamadaService implements GerenciarChamadaUseCase {

    private final ChamadaRepositoryPort chamadaRepositoryPort;
    private final PresencaRepositoryPort presencaRepositoryPort;
    private final ChamadaEventPublisherPort chamadaEventPublisherPort; // grava no Outbox (mesma transação)
    private final TurmaRepositoryPort turmaRepositoryPort;
    private final AcessoTurma acessoTurma;

    @Override
    public Chamada criar(Chamada chamada) {
        acessoTurma.verificar(chamada.getTurmaId());
        if (chamadaRepositoryPort.existe(chamada.getTurmaId(), chamada.getData(), chamada.getHorario())) {
            throw new RegraNegocioException("Já existe chamada para a turma " + chamada.getTurmaId()
                    + " em " + chamada.getData() + " às " + chamada.getHorario() + ".");
        }
        chamada.setStatus(StatusChamada.ABERTA);
        return chamadaRepositoryPort.salvar(chamada);
    }

    @Override
    public Chamada buscarPorId(Long id) {
        Chamada chamada = buscarOuFalhar(id);
        acessoTurma.verificar(chamada.getTurmaId());
        return chamada;
    }

    @Override
    public List<Chamada> listarPorTurma(Long turmaId) {
        acessoTurma.verificar(turmaId);
        return chamadaRepositoryPort.listarPorTurma(turmaId);
    }

    /**
     * Status FINALIZADA, aulas ministradas da turma e evento (Outbox) na MESMA transação.
     * O OutboxRelay envia o evento ao Service Bus depois (consumido pelo aluno-service);
     * se o Service Bus cair, o evento não se perde.
     * Idempotente: finalizar de novo não gera evento duplicado.
     */
    @Override
    @Transactional
    public void finalizar(Long id) {
        Chamada chamada = buscarPorId(id);
        if (chamada.getStatus() == StatusChamada.FINALIZADA) {
            return;
        }
        chamada.setStatus(StatusChamada.FINALIZADA);
        Chamada chamadaSalva = chamadaRepositoryPort.salvar(chamada);

        // Mesma transação: a turma conta a aula ministrada sem depender do próprio evento
        turmaRepositoryPort.incrementarAulasMinistradas(chamadaSalva.getTurmaId());

        chamadaEventPublisherPort.publicarChamadaFinalizada(new ChamadaFinalizadaEvent(
                chamadaSalva.getId(),
                chamadaSalva.getTurmaId(),
                chamadaSalva.getData(),
                chamadaSalva.getStatus().name()));
    }

    @Override
    @Transactional
    public void excluir(Long id) {
        Chamada chamada = buscarPorId(id);
        if (chamada.getStatus() == StatusChamada.FINALIZADA) {
            throw new RegraNegocioException("Não é possível excluir uma chamada finalizada.");
        }
        presencaRepositoryPort.excluirPorChamada(id);
        chamadaRepositoryPort.excluir(id);
    }

    Chamada buscarOuFalhar(Long id) {
        return chamadaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new NaoEncontradoException("Chamada " + id + " não encontrada."));
    }
}
