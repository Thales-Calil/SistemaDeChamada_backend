package com.sistema.chamada.application.ports.in;

import com.sistema.chamada.domain.model.FrequenciaAluno;

/** Frequência de um aluno em uma turma (visão do próprio aluno). */
public record FrequenciaTurma(Long turmaId, String turmaNome, String disciplinaNome, FrequenciaAluno frequencia) {
}
