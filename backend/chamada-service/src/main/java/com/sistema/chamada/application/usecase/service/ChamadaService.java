package com.sistema.chamada.application.usecase.service;

import com.sistema.chamada.application.ports.in.GerenciarChamadaUseCase;
import com.sistema.chamada.application.ports.out.ChamadaEventPublisherPort;
import com.sistema.chamada.application.ports.out.ChamadaRepositoryPort;
import com.sistema.chamada.domain.event.ChamadaFinalizadaEvent;
import com.sistema.chamada.domain.model.Chamada;
import com.sistema.chamada.domain.model.StatusChamada;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChamadaService implements GerenciarChamadaUseCase {

    private final ChamadaRepositoryPort chamadaRepositoryPort;
    private final ChamadaEventPublisherPort chamadaEventPublisherPort; // Porta de Saída do Evento

    @Override
    public Chamada criar(Chamada chamada) {
        chamada.setStatus(StatusChamada.ABERTA);
        return chamadaRepositoryPort.salvar(chamada);
    }

    @Override
    public Chamada buscarPorId(Long id) {
        return chamadaRepositoryPort.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Chamada não encontrada"));
    }

    @Override
    public List<Chamada> listarPorTurma(Long turmaId) {
        return chamadaRepositoryPort.listarPorTurma(turmaId);
    }

    @Override
    public void finalizar(Long id) {
        Chamada chamada = buscarPorId(id);
        chamada.setStatus(StatusChamada.FINALIZADA);
        Chamada chamadaSalva = chamadaRepositoryPort.salvar(chamada);

        // Disparo assíncrono do evento de domínio via Porta Hexagonal
        ChamadaFinalizadaEvent evento = new ChamadaFinalizadaEvent(
                chamadaSalva.getId(),
                chamadaSalva.getTurmaId(),
                chamadaSalva.getData(),
                chamadaSalva.getStatus().name()
        );
        chamadaEventPublisherPort.publicarChamadaFinalizada(evento);
    }

    @Override
    public void excluir(Long id) {
        Chamada chamada = buscarPorId(id);
        if (chamada.getStatus() == StatusChamada.FINALIZADA) {
            throw new RuntimeException("Não é possível excluir uma chamada finalizada");
        }
        chamadaRepositoryPort.excluir(id);
    }
}