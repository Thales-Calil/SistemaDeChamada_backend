package com.sistema.aluno.infrastructure.adapters.in.web;

import com.sistema.aluno.application.ports.in.GerenciarAlunoUseCase;
import com.sistema.aluno.domain.model.Aluno;
import com.sistema.aluno.infrastructure.adapters.in.web.dto.AlunoRequestDTO;
import com.sistema.aluno.infrastructure.adapters.in.web.dto.AlunoResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@RequiredArgsConstructor
public class AlunoController {

    // INJETA A PORTA (Interface), NÃO O SERVICE CONCRETO
    private final GerenciarAlunoUseCase gerenciarAlunoUseCase;

    /**
     * ADMIN: todos | PROFESSOR: alunos das suas turmas | ALUNO: só ele.
     * Filtro opcional: /api/alunos?ids=1,2,3 (ex.: alunos de uma turma).
     */
    @GetMapping
    public ResponseEntity<List<AlunoResponseDTO>> listar(@RequestParam(name = "ids", required = false) List<Long> ids) {
        return ResponseEntity.ok(gerenciarAlunoUseCase.listar(ids).stream().map(AlunoResponseDTO::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(new AlunoResponseDTO(gerenciarAlunoUseCase.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<AlunoResponseDTO> cadastrar(@Valid @RequestBody AlunoRequestDTO dto) {
        Aluno alunoSalvo = gerenciarAlunoUseCase.cadastrar(paraDominio(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(new AlunoResponseDTO(alunoSalvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody AlunoRequestDTO dto) {
        return ResponseEntity.ok(new AlunoResponseDTO(gerenciarAlunoUseCase.atualizar(id, paraDominio(dto))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        gerenciarAlunoUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }

    private static Aluno paraDominio(AlunoRequestDTO dto) {
        Aluno aluno = new Aluno();
        aluno.setNome(dto.getNome().trim());
        aluno.setMatricula(dto.getMatricula().trim());
        aluno.setEmail(dto.getEmail().trim().toLowerCase());
        return aluno;
    }
}
