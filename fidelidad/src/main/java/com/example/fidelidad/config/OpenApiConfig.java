package com.example.fidelidad.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig {
@Bean
    public OpenAPI openAPI() {
        final String securitySchemeName = "bearerAuth"; //
        
        return new OpenAPI()
                .info(new Info()
                        .title("API de Fidelidad") //
                        .version("1.0") //
                        .description("Documentación del microservicio para la acumulación y gestión de puntos por compras de café.")) //
                        .addServersItem(new Server().url("http://localhost:8080")) 
                        .addSecurityItem(new SecurityRequirement().addList(securitySchemeName)) //
                .components(new Components()
                        .addSecuritySchemes(securitySchemeName,
                                new SecurityScheme()            
                                        .name(securitySchemeName) //
                                        .type(SecurityScheme.Type.HTTP) //                               
                                        .scheme("bearer") //
                                        .bearerFormat("JWT"))); //
    }
}