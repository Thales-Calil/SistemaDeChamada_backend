package com.sistema.turma.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TurmaRequestDTO {

    @NotBlank(message = "Nome da turma é obrigatório")
    private String nome;

    @NotNull(message = "Professor é obrigatório")
    private Long professorId;

    @NotNull(message = "Disciplina é obrigatória")
    private Long disciplinaId;
}