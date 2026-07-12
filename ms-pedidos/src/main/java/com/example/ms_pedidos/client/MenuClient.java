package com.example.ms_pedidos.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class MenuClient {

    private final WebClient webClient;

    // 🔗 Nombre lógico registrado en Eureka (spring.application.name del microservicio 'menu').
    private static final String MENU_URL = "http://menu/api/productos/";

    public boolean existeProducto(Integer productoId) {

        try {
            log.info("Consultando 'menu' para validar existencia del producto {}", productoId);

            Boolean existe = webClient.get()
                    .uri(MENU_URL + productoId + "/existe")
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            return Boolean.TRUE.equals(existe);

        } catch (WebClientResponseException.NotFound e) {
            log.warn("El producto {} no existe en 'menu'", productoId);
            return false;
        } catch (Exception e) {
            log.error("Error conectando con 'menu': {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el microservicio de menú");
        }
    }
}
