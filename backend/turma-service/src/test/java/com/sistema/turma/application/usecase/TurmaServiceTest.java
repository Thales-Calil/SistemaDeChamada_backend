package com.sistema.turma.application.usecase;

import com.sistema.turma.application.ports.out.DisciplinaRepositoryPort;
import com.sistema.turma.application.ports.out.ProfessorRepositoryPort;
import com.sistema.turma.application.ports.out.TurmaRepositoryPort;
import com.sistema.turma.domain.model.Disciplina;
import com.sistema.turma.domain.model.Professor;
import com.sistema.turma.domain.model.Turma;
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
class TurmaServiceTest {

    @Mock
    private TurmaRepositoryPort turmaRepositoryPort;

    @Mock
    private ProfessorRepositoryPort professorRepositoryPort;

    @Mock
    private DisciplinaRepositoryPort disciplinaRepositoryPort;

    @InjectMocks
    private TurmaService turmaService;

    private Turma turma;
    private Professor professor;
    private Disciplina disciplina;

    @BeforeEach
    void setUp() {
        professor = new Professor();
        professor.setId(5L);

        disciplina = new Disciplina();
        disciplina.setId(10L);

        turma = new Turma();
        turma.setId(1L);
        turma.setNome("Arquitetura de Soluções");
        turma.setProfessor(professor);
        turma.setDisciplina(disciplina);
    }

    @Test
    void deveCadastrarTurma() {
        when(professorRepositoryPort.buscarPorId(5L)).thenReturn(Optional.of(professor));
        when(disciplinaRepositoryPort.buscarPorId(10L)).thenReturn(Optional.of(disciplina));
        when(turmaRepositoryPort.salvar(any(Turma.class))).thenReturn(turma);

        Turma resultado = turmaService.cadastrar(turma);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Arquitetura de Soluções");
        verify(turmaRepositoryPort, times(1)).salvar(turma);
    }

    @Test
    void deveBuscarTurmaPorId() {
        when(turmaRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(turma));

        Turma resultado = turmaService.buscarPorId(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNome()).isEqualTo("Arquitetura de Soluções");
        verify(turmaRepositoryPort, times(1)).buscarPorId(1L);
    }
}