package com.sistema.turma;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling // OutboxRelay: envia os eventos ChamadaFinalizada ao Service Bus a cada 5 s
public class TurmaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(TurmaServiceApplication.class, args);
    }

}
