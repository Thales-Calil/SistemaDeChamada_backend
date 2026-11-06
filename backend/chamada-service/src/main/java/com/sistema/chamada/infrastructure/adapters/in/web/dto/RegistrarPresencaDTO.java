package com.sistema.chamada.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RegistrarPresencaDTO {

    @NotNull(message = "Aluno é obrigatório")
    private Long alunoId;

    @NotNull(message = "Presença é obrigatória")
    private Boolean presente;
}