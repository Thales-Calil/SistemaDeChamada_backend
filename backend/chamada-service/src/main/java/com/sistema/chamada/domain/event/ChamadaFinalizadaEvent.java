package com.sistema.chamada.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ChamadaFinalizadaEvent {
    private Long chamadaId;
    private Long turmaId;
    private LocalDate data;
    private String status;
    private LocalDateTime dataHoraProcessamento;

    public ChamadaFinalizadaEvent(Long chamadaId, Long turmaId, LocalDate data, String status) {
        this.chamadaId = chamadaId;
        this.turmaId = turmaId;
        this.data = data;
        this.status = status;
        this.dataHoraProcessamento = LocalDateTime.now();
    }
}