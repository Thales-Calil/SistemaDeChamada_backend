package com.sistema.turma.infrastructure.adapters.in.web.controller;

import com.sistema.turma.application.ports.in.GerenciarProfessorUseCase;
import com.sistema.turma.domain.model.Professor;
import com.sistema.turma.infrastructure.adapters.in.web.dto.ProfessorRequestDTO;
import com.sistema.turma.infrastructure.adapters.in.web.dto.ProfessorResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/professores")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ProfessorController {

    private final GerenciarProfessorUseCase gerenciarProfessorUseCase;

    @GetMapping
    public ResponseEntity<List<ProfessorResponseDTO>> listarTodos() {
        List<ProfessorResponseDTO> response = gerenciarProfessorUseCase.listarTodos()
                .stream()
                .map(ProfessorResponseDTO::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessorResponseDTO> buscarPorId(@PathVariable Long id) {
        Professor professor = gerenciarProfessorUseCase.buscarPorId(id);
        return ResponseEntity.ok(new ProfessorResponseDTO(professor));
    }

    @PostMapping
    public ResponseEntity<ProfessorResponseDTO> cadastrar(@Valid @RequestBody ProfessorRequestDTO dto) {
        Professor professorDomain = new Professor();
        professorDomain.setNome(dto.getNome());
        professorDomain.setEmail(dto.getEmail());

        Professor professorSalvo = gerenciarProfessorUseCase.cadastrar(professorDomain);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ProfessorResponseDTO(professorSalvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessorResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProfessorRequestDTO dto) {
        Professor professorDomain = new Professor();
        professorDomain.setNome(dto.getNome());
        professorDomain.setEmail(dto.getEmail());

        Professor professorAtualizado = gerenciarProfessorUseCase.atualizar(id, professorDomain);

        return ResponseEntity.ok(new ProfessorResponseDTO(professorAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        gerenciarProfessorUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }
}