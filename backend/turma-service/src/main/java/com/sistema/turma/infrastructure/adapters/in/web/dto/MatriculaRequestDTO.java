package com.sistema.turma.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MatriculaRequestDTO {

    @NotNull(message = "Aluno é obrigatório")
    private Long alunoId;
}
