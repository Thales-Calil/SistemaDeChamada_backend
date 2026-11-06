package com.sistema.chamada.domain.model;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class Chamada {

    private Long id;

    private Long turmaId;

    private LocalDate data;

    private LocalTime horario;

    private StatusChamada status;
}