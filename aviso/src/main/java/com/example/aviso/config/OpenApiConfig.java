package com.example.aviso.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                // 1. Información del panel del Microservicio
                .info(new Info()
                        .title("Módulo de Avisos y Notificaciones - Cafetería")
                        .version("1.0.0")
                        .description("API para la emisión y gestión de notificaciones del sistema."))

                // 2. Agregar el candado global de autenticación JWT (Bearer Token)
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BearerAuth",
                                new SecurityScheme()
                                        .name("BearerAuth")
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")
                                        .description("Introduce tu token JWT generado por el microservicio de Usuarios para consumir endpoints protegidos.")));
    }
}
