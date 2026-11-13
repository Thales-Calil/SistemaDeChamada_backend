package com.sistema.turma.infrastructure.adapters.in.web.dto;

import com.sistema.turma.domain.model.Turma;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TurmaResponseDTO {

    private Long id;
    private String nome;
    private Long professorId;
    private String professorNome;
    private Long disciplinaId;
    private String disciplinaNome;
    private int aulasMinistradas;

    public TurmaResponseDTO(Turma turma) {
        this.id = turma.getId();
        this.nome = turma.getNome();

        this.professorId = turma.getProfessor().getId();
        this.professorNome = turma.getProfessor().getNome();

        this.disciplinaId = turma.getDisciplina().getId();
        this.disciplinaNome = turma.getDisciplina().getNome();

        this.aulasMinistradas = turma.getAulasMinistradas();
    }
}