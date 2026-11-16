package com.sistema.turma.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

/**
 * Regras da Arquitetura Hexagonal do turma-service (turmas, matrículas, chamadas e presenças).
 */
class HexagonalArchitectureTest {

    private JavaClasses classesImportadas;

    @BeforeEach
    void setUp() {
        classesImportadas = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.sistema.turma");
    }

    /** 1. O domínio (Turma, Chamada, Presenca, cálculo de frequência) é puro: sem Spring, JPA ou infraestrutura. */
    @Test
    void dominioNaoDeveDependerDaInfraestruturaNemDoSpring() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..infrastructure..", "..application..", "org.springframework..", "jakarta.persistence..")
                .check(classesImportadas);
    }

    /** 2. Os casos de uso só falam com o mundo externo por portas (ports.out), nunca pelos adaptadores. */
    @Test
    void aplicacaoNaoDeveDependerDosAdaptadores() {
        noClasses()
                .that().resideInAPackage("..application..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure..")
                .check(classesImportadas);
    }

    /** 3. Controllers (entrada) não acessam persistência, HTTP nem mensageria (saída) diretamente. */
    @Test
    void adaptadoresDeEntradaNaoDevemUsarAdaptadoresDeSaida() {
        noClasses()
                .that().resideInAPackage("..infrastructure.adapters.in..")
                .should().dependOnClassesThat()
                .resideInAPackage("..infrastructure.adapters.out..")
                .check(classesImportadas);
    }
}
