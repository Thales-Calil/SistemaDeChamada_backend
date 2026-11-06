package com.sistema.turma.infrastructure.adapters.in.web.dto;

import com.sistema.turma.domain.model.Professor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProfessorResponseDTO {

    private Long id;
    private String nome;
    private String email;

    public ProfessorResponseDTO(Professor professor) {
        this.id = professor.getId();
        this.nome = professor.getNome();
        this.email = professor.getEmail();
    }
}