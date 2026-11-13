package com.sistema.chamada.infrastructure.adapters.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** 3.1: chamada inteira de uma vez. Ex.: { "presencas": [{"alunoId":1,"presente":true}], "finalizar": true } */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LotePresencaDTO {

    @NotEmpty(message = "Informe a presença de ao menos um aluno")
    @Valid
    private List<RegistrarPresencaDTO> presencas;

    /** Se true, finaliza a chamada na mesma transação. */
    private boolean finalizar;
}
