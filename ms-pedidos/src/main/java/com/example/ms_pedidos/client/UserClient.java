package com.example.ms_pedidos.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserClient {

    private final WebClient webClient;

    // 🔗 Nombre lógico registrado en Eureka (spring.application.name del microservicio 'user').
    private static final String USER_URL = "http://user/api/auth/id/";

    public boolean existeUsuario(Integer usuarioId) {

        try {
            log.info("Consultando 'user' para validar existencia del usuario {}", usuarioId);

            Boolean existe = webClient.get()
                    .uri(USER_URL + usuarioId + "/existe")
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            return Boolean.TRUE.equals(existe);

        } catch (WebClientResponseException.NotFound e) {
            log.warn("El usuario {} no existe en 'user'", usuarioId);
            return false;
        } catch (Exception e) {
            log.error("Error conectando con 'user': {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el microservicio de usuarios");
        }
    }
}
