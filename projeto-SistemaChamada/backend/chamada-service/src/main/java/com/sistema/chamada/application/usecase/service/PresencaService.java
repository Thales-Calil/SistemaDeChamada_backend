package com.sistema.chamada.application.usecase.service;

import com.sistema.chamada.application.ports.in.GerenciarPresencaUseCase;
import com.sistema.chamada.application.ports.out.ChamadaRepositoryPort;
import com.sistema.chamada.application.ports.out.PresencaRepositoryPort;
import com.sistema.chamada.domain.model.Chamada;
import com.sistema.chamada.domain.model.FrequenciaAluno;
import com.sistema.chamada.domain.model.Presenca;
import com.sistema.chamada.domain.model.StatusChamada;
import com.sistema.chamada.domain.service.CalculadorFrequenciaDomainService;
import com.sistema.chamada.infrastructure.adapters.in.web.dto.PresencaTurmaDTO;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PresencaService implements GerenciarPresencaUseCase {

    private final ChamadaRepositoryPort chamadaRepositoryPort;
    private final PresencaRepositoryPort presencaRepositoryPort;

    // Instancia o serviço de domínio puro
    private final CalculadorFrequenciaDomainService calculadorDomainService = new CalculadorFrequenciaDomainService();

    // 1. Implementação do método registrarPresenca
    @Override
    public Presenca registrarPresenca(Long chamadaId, Long alunoId, Boolean presente) {
        Chamada chamada = chamadaRepositoryPort.buscarPorId(chamadaId)
                .orElseThrow(() -> new RuntimeException("Chamada não encontrada"));

        if (chamada.getStatus() == StatusChamada.FINALIZADA) {
            throw new RuntimeException("Não é possível registrar presença em uma chamada finalizada");
        }

        Presenca presenca = presencaRepositoryPort.buscarPorChamadaEAluno(chamadaId, alunoId)
                .orElse(new Presenca());

        presenca.setChamadaId(chamadaId);
        presenca.setAlunoId(alunoId);
        presenca.setPresente(presente);

        return presencaRepositoryPort.salvar(presenca);
    }

    // 2. Implementação do método atualizarPresenca
    @Override
    public Presenca atualizarPresenca(Long chamadaId, Long alunoId, Boolean presente) {
        Chamada chamada = chamadaRepositoryPort.buscarPorId(chamadaId)
                .orElseThrow(() -> new RuntimeException("Chamada não encontrada"));

        if (chamada.getStatus() == StatusChamada.FINALIZADA) {
            throw new RuntimeException("Não é possível alterar presença de uma chamada finalizada");
        }

        Presenca presenca = presencaRepositoryPort.buscarPorChamadaEAluno(chamadaId, alunoId)
                .orElseThrow(() -> new RuntimeException("Presença do aluno não encontrada"));

        presenca.setPresente(presente);

        return presencaRepositoryPort.salvar(presenca);
    }

    // 3. Implementação do método calcularPorTurma
    @Override
    public List<PresencaTurmaDTO> calcularPorTurma(Long turmaId) {
        List<Chamada> chamadasFinalizadas = chamadaRepositoryPort.listarPorTurma(turmaId)
                .stream()
                .filter(chamada -> chamada.getStatus() == StatusChamada.FINALIZADA)
                .toList();

        if (chamadasFinalizadas.isEmpty()) {
            return List.of();
        }

        List<Long> idsChamadas = chamadasFinalizadas.stream().map(Chamada::getId).toList();
        List<Presenca> presencas = presencaRepositoryPort.buscarPorChamadas(idsChamadas);

        Map<Long, Long> totalPresencasPorAluno = presencas.stream()
                .filter(presenca -> Boolean.TRUE.equals(presenca.getPresente()))
                .collect(Collectors.groupingBy(Presenca::getAlunoId, Collectors.counting()));

        int totalChamadas = chamadasFinalizadas.size();

        return presencas.stream()
                .map(Presenca::getAlunoId)
                .distinct()
                .map(alunoId -> {
                    int presencasAluno = totalPresencasPorAluno.getOrDefault(alunoId, 0L).intValue();

                    // O Domínio resolve a regra de negócio matemática
                    FrequenciaAluno frequencia = calculadorDomainService.calcular(alunoId, totalChamadas, presencasAluno);

                    return new PresencaTurmaDTO(frequencia);
                })
                .toList();
    }
}