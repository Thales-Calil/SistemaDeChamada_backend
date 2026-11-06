package com.sistema.chamada.infrastructure.adapters.in.web.controller;

import com.sistema.chamada.application.ports.in.GerenciarChamadaUseCase;
import com.sistema.chamada.application.ports.in.GerenciarPresencaUseCase;
import com.sistema.chamada.domain.model.Chamada;
import com.sistema.chamada.domain.model.Presenca;
import com.sistema.chamada.infrastructure.adapters.in.web.dto.*;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/chamadas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChamadaController {

    private final GerenciarChamadaUseCase gerenciarChamadaUseCase;
    private final GerenciarPresencaUseCase gerenciarPresencaUseCase; // Injeta a porta para o endpoint de presença da chamada

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
        Chamada chamada = gerenciarChamadaUseCase.buscarPorId(id);
        return ResponseEntity.ok(new ChamadaResponseDTO(chamada));
    }

    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<ChamadaResponseDTO>> listarPorTurma(@PathVariable Long turmaId) {
        List<ChamadaResponseDTO> response = gerenciarChamadaUseCase.listarPorTurma(turmaId)
                .stream().map(ChamadaResponseDTO::new).toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/presencas")
    public ResponseEntity<PresencaResponseDTO> registrarPresenca(
            @PathVariable Long id, @Valid @RequestBody RegistrarPresencaDTO dto) {
        Presenca presencaSalva = gerenciarPresencaUseCase.registrarPresenca(id, dto.getAlunoId(), dto.getPresente());
        return ResponseEntity.status(HttpStatus.CREATED).body(new PresencaResponseDTO(presencaSalva));
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