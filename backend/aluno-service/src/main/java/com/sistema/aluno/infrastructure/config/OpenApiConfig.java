package com.sistema.aluno.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "API de Aluno",
                version = "1.0.0",
                description = "Documentação dos endpoints do microserviço de aluno"
        )
)
public class OpenApiConfig {
}