package com.sistema.turma.architecture;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class HexagonalArchitectureTest {

    private JavaClasses classesImportadas;

    @BeforeEach
    void setUp() {
        classesImportadas = new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages("com.sistema.turma");
    }

    @Test
    void dominioNaoDeveDependerDaInfraestruturaNemDoSpring() {
        noClasses()
                .that().resideInAPackage("..domain..")
                .should().dependOnClassesThat()
                .resideInAnyPackage("..infrastructure..", "org.springframework..")
                .check(classesImportadas);
    }
}