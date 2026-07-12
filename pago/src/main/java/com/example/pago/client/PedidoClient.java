package com.example.pago.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoClient {

    private final RestTemplate restTemplate;

    // 🔗 Nombre lógico registrado en Eureka (spring.application.name de ms-pedidos).
    // El RestTemplate está anotado @LoadBalanced, así que resuelve este nombre
    // automáticamente a la instancia real (funciona igual en local y en Docker).
    private static final String URL_PEDIDOS = "http://ms-pedidos/api/pedidos/{id}";

    public boolean existePedido(Integer pedidoId) {
        try {
            log.info("[INTER-SERVICIO] Consultando 'ms-pedidos' para validar el pedido {}", pedidoId);
            restTemplate.getForObject(URL_PEDIDOS, Object.class, pedidoId);
            return true;
        } catch (HttpClientErrorException.NotFound e) {
            log.warn("[INTER-SERVICIO] El pedido {} no existe en 'ms-pedidos'", pedidoId);
            return false;
        } catch (Exception e) {
            log.error("[INTER-SERVICIO] Error conectando con 'ms-pedidos': {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con el microservicio de pedidos");
        }
    }
}
