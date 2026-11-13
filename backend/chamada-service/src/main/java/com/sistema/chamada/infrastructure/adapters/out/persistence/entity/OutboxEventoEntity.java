package com.sistema.chamada.infrastructure.adapters.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

/** 2.4 - Padrão Outbox: evento gravado junto com a chamada e enviado depois ao Service Bus. */
@Data
@Entity(name = "outbox_eventos")
public class OutboxEventoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String tipo;

    /** Chave única do evento (também usada como MessageId no Service Bus). */
    @Column(nullable = false, unique = true, length = 200)
    private String chave;

    @Column(nullable = false)
    private String payload;

    @Column(name = "criado_em", nullable = false)
    private LocalDateTime criadoEm;

    @Column(name = "enviado_em")
    private LocalDateTime enviadoEm;

    @Column(nullable = false)
    private int tentativas;

    @Column(name = "ultimo_erro", length = 1000)
    private String ultimoErro;
}
