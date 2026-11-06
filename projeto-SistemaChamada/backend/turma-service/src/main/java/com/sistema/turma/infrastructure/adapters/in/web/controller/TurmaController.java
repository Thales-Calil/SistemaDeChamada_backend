package com.sistema.turma.infrastructure.adapters.in.web.controller;

import com.sistema.turma.application.ports.in.GerenciarTurmaUseCase;
import com.sistema.turma.domain.model.Disciplina;
import com.sistema.turma.domain.model.Professor;
import com.sistema.turma.domain.model.Turma;
import com.sistema.turma.infrastructure.adapters.in.web.dto.TurmaRequestDTO;
import com.sistema.turma.infrastructure.adapters.in.web.dto.TurmaResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class TurmaController {

    private final GerenciarTurmaUseCase gerenciarTurmaUseCase;

    @GetMapping
    public ResponseEntity<List<TurmaResponseDTO>> listarTodos() {
        List<TurmaResponseDTO> response = gerenciarTurmaUseCase.listarTodos()
                .stream()
                .map(TurmaResponseDTO::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponseDTO> buscarPorId(@PathVariable Long id) {
        Turma turma = gerenciarTurmaUseCase.buscarPorId(id);
        return ResponseEntity.ok(new TurmaResponseDTO(turma));
    }

    @PostMapping
    public ResponseEntity<TurmaResponseDTO> cadastrar(@Valid @RequestBody TurmaRequestDTO dto) {
        // Converte DTO para Domínio (Montagem inicial com os IDs)
        Turma turmaDomain = new Turma();
        turmaDomain.setNome(dto.getNome());

        Professor professor = new Professor();
        professor.setId(dto.getProfessorId());
        turmaDomain.setProfessor(professor);

        Disciplina disciplina = new Disciplina();
        disciplina.setId(dto.getDisciplinaId());
        turmaDomain.setDisciplina(disciplina);

        Turma turmaSalva = gerenciarTurmaUseCase.cadastrar(turmaDomain);
        return ResponseEntity.status(HttpStatus.CREATED).body(new TurmaResponseDTO(turmaSalva));
    }
}