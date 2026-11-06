package com.sistema.chamada.infrastructure.adapters.in.web.dto;

import com.sistema.chamada.domain.model.Chamada;
import com.sistema.chamada.domain.model.StatusChamada;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ChamadaResponseDTO {

    private Long id;
    private Long turmaId;
    private LocalDate data;
    private LocalTime horario;
    private StatusChamada status;

    public ChamadaResponseDTO(Chamada chamada) {
        this.id = chamada.getId();
        this.turmaId = chamada.getTurmaId();
        this.data = chamada.getData();
        this.horario = chamada.getHorario();
        this.status = chamada.getStatus();
    }
}