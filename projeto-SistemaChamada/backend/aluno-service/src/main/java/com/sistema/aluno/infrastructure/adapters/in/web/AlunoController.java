package com.sistema.aluno.infrastructure.adapters.in.web;

import com.sistema.aluno.application.ports.in.GerenciarAlunoUseCase;
import com.sistema.aluno.domain.model.Aluno;
import com.sistema.aluno.infrastructure.adapters.in.web.dto.AlunoRequestDTO;
import com.sistema.aluno.infrastructure.adapters.in.web.dto.AlunoResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("/api/alunos")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AlunoController {

    // INJETA A PORTA (Interface), NÃO O SERVICE CONCRETO
    private final GerenciarAlunoUseCase gerenciarAlunoUseCase;

    @GetMapping
    public ResponseEntity<List<AlunoResponseDTO>> listarTodos() {
        List<AlunoResponseDTO> response = gerenciarAlunoUseCase.listarTodos()
                .stream()
                .map(AlunoResponseDTO::new)
                .toList();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> buscarPorId(@PathVariable String id) {
        Aluno aluno = gerenciarAlunoUseCase.buscarPorId(id);
        return ResponseEntity.ok(new AlunoResponseDTO(aluno));
    }

    @PostMapping
    public ResponseEntity<AlunoResponseDTO> cadastrar(@Valid @RequestBody AlunoRequestDTO dto) {
        Aluno alunoDomain = new Aluno();
        alunoDomain.setNome(dto.getNome());
        alunoDomain.setMatricula(dto.getMatricula());
        alunoDomain.setEmail(dto.getEmail());

        Aluno alunoSalvo = gerenciarAlunoUseCase.cadastrar(alunoDomain);

        return ResponseEntity.status(HttpStatus.CREATED).body(new AlunoResponseDTO(alunoSalvo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlunoResponseDTO> atualizar(@PathVariable String id, @Valid @RequestBody AlunoRequestDTO dto) {
        Aluno alunoDomain = new Aluno();
        alunoDomain.setNome(dto.getNome());
        alunoDomain.setMatricula(dto.getMatricula());
        alunoDomain.setEmail(dto.getEmail());

        Aluno alunoAtualizado = gerenciarAlunoUseCase.atualizar(id, alunoDomain);

        return ResponseEntity.ok(new AlunoResponseDTO(alunoAtualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable String id) {
        gerenciarAlunoUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }
}