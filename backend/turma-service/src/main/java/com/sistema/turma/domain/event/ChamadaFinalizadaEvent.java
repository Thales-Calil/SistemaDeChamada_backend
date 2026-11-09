package com.sistema.turma.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChamadaFinalizadaEvent {
    private Long chamadaId;
    private Long turmaId;
    private LocalDate data;
    private String status;
    private LocalDateTime dataHoraProcessamento;
}