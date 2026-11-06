package com.sistema.turma.infrastructure.adapters.in.web.controller;

import com.sistema.turma.application.ports.in.GerenciarDisciplinaUseCase;
import com.sistema.turma.domain.model.Disciplina;
import com.sistema.turma.infrastructure.adapters.in.web.dto.DisciplinaRequestDTO;
import com.sistema.turma.infrastructure.adapters.in.web.dto.DisciplinaResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/disciplinas")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DisciplinaController {

    // INJETA A PORTA DE ENTRADA, NÃO A CLASSE CONCRETA
    private final GerenciarDisciplinaUseCase gerenciarDisciplinaUseCase;

    @GetMapping
    public ResponseEntity<List<DisciplinaResponseDTO>> listarTodos() {
        List<DisciplinaResponseDTO> response = gerenciarDisciplinaUseCase.listarTodos()
                .stream()
                .map(DisciplinaResponseDTO::new) // O seu DTO já deve ter o construtor que recebe a classe de domínio
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisciplinaResponseDTO> buscarPorId(@PathVariable Long id) {
        Disciplina disciplina = gerenciarDisciplinaUseCase.buscarPorId(id);
        return ResponseEntity.ok(new DisciplinaResponseDTO(disciplina));
    }

    @PostMapping
    public ResponseEntity<DisciplinaResponseDTO> cadastrar(@Valid @RequestBody DisciplinaRequestDTO dto) {
        Disciplina disciplinaDomain = new Disciplina();
        disciplinaDomain.setNome(dto.getNome());
        disciplinaDomain.setCodigo(dto.getCodigo());

        Disciplina disciplinaSalva = gerenciarDisciplinaUseCase.cadastrar(disciplinaDomain);

        return ResponseEntity.status(HttpStatus.CREATED).body(new DisciplinaResponseDTO(disciplinaSalva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DisciplinaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody DisciplinaRequestDTO dto) {
        Disciplina disciplinaDomain = new Disciplina();
        disciplinaDomain.setNome(dto.getNome());
        disciplinaDomain.setCodigo(dto.getCodigo());

        Disciplina disciplinaAtualizada = gerenciarDisciplinaUseCase.atualizar(id, disciplinaDomain);

        return ResponseEntity.ok(new DisciplinaResponseDTO(disciplinaAtualizada));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        gerenciarDisciplinaUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }
}