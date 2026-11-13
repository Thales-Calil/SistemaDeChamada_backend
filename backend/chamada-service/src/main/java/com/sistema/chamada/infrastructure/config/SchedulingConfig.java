package com.sistema.chamada.infrastructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

/** Habilita o @Scheduled do OutboxRelay. */
@Configuration
@EnableScheduling
public class SchedulingConfig {
}
