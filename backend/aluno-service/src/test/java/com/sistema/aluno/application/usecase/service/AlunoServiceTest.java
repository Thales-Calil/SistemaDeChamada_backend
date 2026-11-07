package com.sistema.aluno.application.usecase.service;

import com.sistema.aluno.application.ports.out.AlunoRepositoryPort;
import com.sistema.aluno.domain.model.Aluno;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlunoServiceTest {

    @Mock
    private AlunoRepositoryPort alunoRepositoryPort;

    @InjectMocks
    private AlunoService alunoService;

    private Aluno aluno;

    @BeforeEach
    void setUp() {
        aluno = new Aluno();
        aluno.setId("65a1f2b3c4d5e6f7a8b9c0d1");
        aluno.setNome("Thales Calil");
        aluno.setMatricula("2026001");
        aluno.setEmail("thales@pucpr.edu.br");
    }

    @Test
    void deveCriarAluno() {
        when(alunoRepositoryPort.salvar(any(Aluno.class))).thenReturn(aluno);

        Aluno resultado = alunoService.cadastrar(aluno);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo("65a1f2b3c4d5e6f7a8b9c0d1");
        verify(alunoRepositoryPort, times(1)).salvar(aluno);
    }

    @Test
    void deveBuscarAlunoPorId() {
        when(alunoRepositoryPort.buscarPorId("65a1f2b3c4d5e6f7a8b9c0d1")).thenReturn(Optional.of(aluno));

        Aluno resultado = alunoService.buscarPorId("65a1f2b3c4d5e6f7a8b9c0d1");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getMatricula()).isEqualTo("2026001");
        verify(alunoRepositoryPort, times(1)).buscarPorId("65a1f2b3c4d5e6f7a8b9c0d1");
    }
}