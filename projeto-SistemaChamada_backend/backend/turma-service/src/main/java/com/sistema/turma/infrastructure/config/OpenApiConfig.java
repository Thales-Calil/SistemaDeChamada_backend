package com.sistema.turma.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API de Turma",
                version = "1.0.0",
                description = "Documentação dos endpoints do microserviço de turma"
        )
)
public class OpenApiConfig {
}