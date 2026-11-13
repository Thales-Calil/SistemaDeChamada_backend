package com.sistema.chamada.infrastructure.adapters.in.web.controller;

import com.sistema.chamada.application.ports.in.GerenciarChamadaUseCase;
import com.sistema.chamada.application.ports.in.GerenciarPresencaUseCase;
import com.sistema.chamada.application.ports.in.ItemPresenca;
import com.sistema.chamada.domain.model.Chamada;
import com.sistema.chamada.domain.model.Presenca;
import com.sistema.chamada.infrastructure.adapters.in.web.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chamadas")
@RequiredArgsConstructor
public class ChamadaController {

    private final GerenciarChamadaUseCase gerenciarChamadaUseCase;
    private final GerenciarPresencaUseCase gerenciarPresencaUseCase;

    @PostMapping
    public ResponseEntity<ChamadaResponseDTO> criar(@Valid @RequestBody CriarChamadaDTO dto) {
        Chamada chamadaDomain = new Chamada();
        chamadaDomain.setTurmaId(dto.getTurmaId());
        chamadaDomain.setData(dto.getData());
        chamadaDomain.setHorario(dto.getHorario());

        Chamada chamadaSalva = gerenciarChamadaUseCase.criar(chamadaDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ChamadaResponseDTO(chamadaSalva));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChamadaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(new ChamadaResponseDTO(gerenciarChamadaUseCase.buscarPorId(id)));
    }

    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<ChamadaResponseDTO>> listarPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(gerenciarChamadaUseCase.listarPorTurma(turmaId).stream().map(ChamadaResponseDTO::new).toList());
    }

    @GetMapping("/{id}/presencas")
    public ResponseEntity<List<PresencaResponseDTO>> listarPresencas(@PathVariable Long id) {
        return ResponseEntity.ok(gerenciarPresencaUseCase.listarPresencasDaChamada(id).stream().map(PresencaResponseDTO::new).toList());
    }

    @PostMapping("/{id}/presencas")
    public ResponseEntity<PresencaResponseDTO> registrarPresenca(
            @PathVariable Long id, @Valid @RequestBody RegistrarPresencaDTO dto) {
        Presenca presencaSalva = gerenciarPresencaUseCase.registrarPresenca(id, dto.getAlunoId(), dto.getPresente());
        return ResponseEntity.status(HttpStatus.CREATED).body(new PresencaResponseDTO(presencaSalva));
    }

    /** 3.1: registra a presença de todos os alunos de uma vez (tudo ou nada) e opcionalmente finaliza. */
    @PostMapping("/{id}/presencas/lote")
    public ResponseEntity<List<PresencaResponseDTO>> registrarEmLote(
            @PathVariable Long id, @Valid @RequestBody LotePresencaDTO dto) {
        List<ItemPresenca> itens = dto.getPresencas().stream()
                .map(p -> new ItemPresenca(p.getAlunoId(), p.getPresente()))
                .toList();
        List<Presenca> gravadas = gerenciarPresencaUseCase.registrarEmLote(id, itens, dto.isFinalizar());
        return ResponseEntity.ok(gravadas.stream().map(PresencaResponseDTO::new).toList());
    }

    @PutMapping("/{id}/presencas/{alunoId}")
    public ResponseEntity<PresencaResponseDTO> atualizarPresenca(
            @PathVariable Long id, @PathVariable Long alunoId, @Valid @RequestBody RegistrarPresencaDTO dto) {
        Presenca presencaAtualizada = gerenciarPresencaUseCase.atualizarPresenca(id, alunoId, dto.getPresente());
        return ResponseEntity.ok(new PresencaResponseDTO(presencaAtualizada));
    }

    @PutMapping("/{id}/finalizar")
    public ResponseEntity<Void> finalizar(@PathVariable Long id) {
        gerenciarChamadaUseCase.finalizar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        gerenciarChamadaUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
