package com.sistema.chamada.infrastructure.adapters.in.web.controller;

import com.sistema.chamada.application.ports.in.GerenciarPresencaUseCase;
import com.sistema.chamada.infrastructure.adapters.in.web.dto.FrequenciaAlunoTurmaDTO;
import com.sistema.chamada.infrastructure.adapters.in.web.dto.PresencaTurmaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/presencas")
@RequiredArgsConstructor
public class PresencaController {

    private final GerenciarPresencaUseCase gerenciarPresencaUseCase;

    /** Frequência de todos os alunos da turma (ADMIN ou professor da turma). */
    @GetMapping("/turma/{turmaId}")
    public ResponseEntity<List<PresencaTurmaDTO>> calcularPorTurma(@PathVariable Long turmaId) {
        return ResponseEntity.ok(gerenciarPresencaUseCase.calcularPorTurma(turmaId).stream().map(PresencaTurmaDTO::new).toList());
    }

    /** Frequência do aluno em cada turma que cursa (ALUNO: só a própria). */
    @GetMapping("/aluno/{alunoId}")
    public ResponseEntity<List<FrequenciaAlunoTurmaDTO>> calcularPorAluno(@PathVariable Long alunoId) {
        return ResponseEntity.ok(gerenciarPresencaUseCase.calcularPorAluno(alunoId).stream().map(FrequenciaAlunoTurmaDTO::new).toList());
    }
}
