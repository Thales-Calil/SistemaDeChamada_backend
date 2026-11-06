package com.sistema.chamada.infrastructure.adapters.in.web.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class CriarChamadaDTO {

    @NotNull(message = "Turma é obrigatória")
    private Long turmaId;

    @NotNull(message = "Data é obrigatória")
    private LocalDate data;

    @NotNull(message = "Horário é obrigatório")
    private LocalTime horario;
}