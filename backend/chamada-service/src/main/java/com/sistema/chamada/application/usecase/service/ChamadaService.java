package com.sistema.chamada.application.usecase.service;

import com.sistema.chamada.application.exception.NaoEncontradoException;
import com.sistema.chamada.application.exception.RegraNegocioException;
import com.sistema.chamada.application.ports.in.GerenciarChamadaUseCase;
import com.sistema.chamada.application.ports.out.ChamadaEventPublisherPort;
import com.sistema.chamada.application.ports.out.ChamadaRepositoryPort;
import com.sistema.chamada.application.ports.out.PresencaRepositoryPort;
import com.sistema.chamada.domain.event.ChamadaFinalizadaEvent;
import com.sistema.chamada.domain.model.Chamada;
import com.sistema.chamada.domain.model.StatusChamada;
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
     * 2.4: status FINALIZADA e evento são gravados na MESMA transação (Outbox).
     * O OutboxRelay envia ao Service Bus depois; se o Service Bus cair, o evento não se perde.
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
