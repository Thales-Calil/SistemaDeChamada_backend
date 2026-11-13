package com.sistema.turma.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "eventos_processados")
public class EventoProcessadoEntity {

    @Id
    @Column(name = "chave", length = 200)
    private String chave;

    @Column(name = "processado_em", nullable = false)
    private LocalDateTime processadoEm;
}
