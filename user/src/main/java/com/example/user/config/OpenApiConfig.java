package com.example.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;

@Configuration
public class OpenApiConfig { //

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API de Autenticación (User)") 
                        .version("1.0") //
                        .description("Documentación del microservicio de registro, login y gestión de usuarios de la cafetería.")) //
                .addServersItem(new Server().url("http://localhost:8080")); //
    }
}