package com.sistema.chamada.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrarPresencaDTO {

    @NotNull(message = "Aluno é obrigatório")
    private Long alunoId;

    @NotNull(message = "Presença é obrigatória")
    private Boolean presente;
}