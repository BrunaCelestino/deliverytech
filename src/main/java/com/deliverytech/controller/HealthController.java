package com.deliverytech.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.time.LocalDateTime;
import java.util.Map;

@Tag(name = "Aplicação", description = "Endpoints para verificar saúde e informações da aplicação")
@RestController
public class HealthController {

    @Operation(summary = "Saúde da aplicação", description = "Verifica a saúde da aplicação.")
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now().toString(),
            "service", "Delivery API",
            "javaVersion", System.getProperty("java.version")
        );
    }

    @Operation(summary = "informações da aplicação", description = "Retorna as informações da aplicação.")
    @GetMapping("/info")
    public AppInfo info() {
        return new AppInfo(
            "Delivery Tech API",
            "1.0.0",
            "Bruna Massuchini",
            "JDK 21",
            "Spring Boot 3.2.x"
        );
    }

    // Record para demonstrar recurso do Java 14+ (disponível no JDK 21)
    public record AppInfo(
        String application,
        String version,
        String developer,
        String javaVersion,
        String framework
    ) {}
}