package com.sistema.chamada.infrastructure.adapters.in.web.controller;

import com.sistema.chamada.application.ports.in.GerenciarPresencaUseCase;
import com.sistema.chamada.infrastructure.adapters.in.web.dto.PresencaTurmaDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/presencas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class PresencaController {

    private final GerenciarPresencaUseCase gerenciarPresencaUseCase;

    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<PresencaTurmaDTO>> calcularPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(gerenciarPresencaUseCase.calcularPorTurma(turmaId));
    }
}