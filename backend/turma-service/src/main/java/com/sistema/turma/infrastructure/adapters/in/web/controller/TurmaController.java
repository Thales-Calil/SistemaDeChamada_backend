package com.sistema.turma.infrastructure.adapters.in.web.controller;

import com.sistema.turma.application.ports.in.GerenciarMatriculaUseCase;
import com.sistema.turma.application.ports.in.GerenciarTurmaUseCase;
import com.sistema.turma.domain.model.Disciplina;
import com.sistema.turma.domain.model.Professor;
import com.sistema.turma.domain.model.Turma;
import com.sistema.turma.infrastructure.adapters.in.web.dto.MatriculaRequestDTO;
import com.sistema.turma.infrastructure.adapters.in.web.dto.TurmaRequestDTO;
import com.sistema.turma.infrastructure.adapters.in.web.dto.TurmaResponseDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/turmas")
@RequiredArgsConstructor
public class TurmaController {

    private final GerenciarTurmaUseCase gerenciarTurmaUseCase;
    private final GerenciarMatriculaUseCase gerenciarMatriculaUseCase;

    /** ADMIN: todas | PROFESSOR: as suas | ALUNO: as que cursa. */
    @GetMapping
    public ResponseEntity<List<TurmaResponseDTO>> listarTodos() {
        return ResponseEntity.ok(gerenciarTurmaUseCase.listarTodos().stream().map(TurmaResponseDTO::new).toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TurmaResponseDTO> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(new TurmaResponseDTO(gerenciarTurmaUseCase.buscarPorId(id)));
    }

    @PostMapping
    public ResponseEntity<TurmaResponseDTO> cadastrar(@Valid @RequestBody TurmaRequestDTO dto) {
        Turma turmaSalva = gerenciarTurmaUseCase.cadastrar(paraDominio(dto));
        return ResponseEntity.status(HttpStatus.CREATED).body(new TurmaResponseDTO(turmaSalva));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TurmaResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody TurmaRequestDTO dto) {
        return ResponseEntity.ok(new TurmaResponseDTO(gerenciarTurmaUseCase.atualizar(id, paraDominio(dto))));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        gerenciarTurmaUseCase.excluir(id);
        return ResponseEntity.noContent().build();
    }

    // ---------------- Matrículas (aluno <-> turma) ----------------

    /** Ids dos alunos matriculados (ADMIN ou professor da turma). */
    @GetMapping("/{id}/alunos")
    public ResponseEntity<List<Long>> listarAlunos(@PathVariable Long id) {
        return ResponseEntity.ok(gerenciarMatriculaUseCase.listarAlunosDaTurma(id));
    }

    @PostMapping("/{id}/alunos")
    public ResponseEntity<Void> matricular(@PathVariable Long id, @Valid @RequestBody MatriculaRequestDTO dto) {
        gerenciarMatriculaUseCase.matricular(id, dto.getAlunoId());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/{id}/alunos/{alunoId}")
    public ResponseEntity<Void> desmatricular(@PathVariable Long id, @PathVariable Long alunoId) {
        gerenciarMatriculaUseCase.desmatricular(id, alunoId);
        return ResponseEntity.noContent().build();
    }

    /** Turmas que o aluno cursa (ALUNO: só o próprio id). */
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<TurmaResponseDTO>> turmasDoAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(gerenciarMatriculaUseCase.listarTurmasDoAluno(alunoId).stream().map(TurmaResponseDTO::new).toList());
    }

    /** Ids de todos os alunos das turmas de um professor (usado pelo aluno-service para filtrar). */
    @GetMapping("/professor/{professorId}/alunos")
    public ResponseEntity<List<Long>> alunosDoProfessor(@PathVariable Long professorId) {
        return ResponseEntity.ok(gerenciarMatriculaUseCase.listarAlunosDoProfessor(professorId));
    }

    private static Turma paraDominio(TurmaRequestDTO dto) {
        Turma turma = new Turma();
        turma.setNome(dto.getNome());
        Professor professor = new Professor();
        professor.setId(dto.getProfessorId());
        turma.setProfessor(professor);
        Disciplina disciplina = new Disciplina();
        disciplina.setId(dto.getDisciplinaId());
        turma.setDisciplina(disciplina);
        return turma;
    }
}
