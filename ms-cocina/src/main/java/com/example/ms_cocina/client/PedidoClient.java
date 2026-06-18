package com.example.ms_cocina.client;

import com.example.ms_cocina.dto.ApiResponse;
import com.example.ms_cocina.dto.PedidoResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
@RequiredArgsConstructor
@Slf4j
public class PedidoClient {

    private final WebClient webClient;

    private static final String PEDIDOS_URL = "http://localhost:8081/api/pedidos/";

    public boolean existePedido(Integer pedidoId) {

        try {
            log.info("Consultando ms_pedidos para validar pedido {}", pedidoId);

            ApiResponse<PedidoResponseDTO> response = webClient.get()
                    .uri(PEDIDOS_URL + pedidoId)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<PedidoResponseDTO>>() {})
                    .block();

            return response != null
                    && response.isSuccess()
                    && response.getData() != null;

        } catch (WebClientResponseException.NotFound e) {

            log.warn("El pedido {} no existe en ms_pedidos", pedidoId);
            return false;

        } catch (Exception e) {

            log.error("Error conectando con ms_pedidos: {}", e.getMessage());
            throw new RuntimeException("No se pudo conectar con ms_pedidos");
        }
    }
}
