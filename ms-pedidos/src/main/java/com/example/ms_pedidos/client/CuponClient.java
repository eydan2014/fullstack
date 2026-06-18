package com.example.ms_pedidos.client;

import com.example.ms_pedidos.dto.ApiResponse;
import com.example.ms_pedidos.dto.AplicarCuponRequestDTO;
import com.example.ms_pedidos.dto.AplicarCuponResponseDTO;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
@Slf4j
public class CuponClient {

    private final WebClient webClient;

    private static final String CUPONES_URL = "http://localhost:8089/api/cupones/aplicar";

    public AplicarCuponResponseDTO aplicarCupon(String codigo, BigDecimal montoPedido) {

        try {
            log.info("Consultando ms_cupones para aplicar cupón {}", codigo);

            AplicarCuponRequestDTO request = AplicarCuponRequestDTO.builder()
                    .codigo(codigo)
                    .montoPedido(montoPedido)
                    .build();

            ApiResponse<AplicarCuponResponseDTO> response = webClient.post()
                    .uri(CUPONES_URL)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<ApiResponse<AplicarCuponResponseDTO>>() {})
                    .block();

            if (response == null || response.getData() == null) {
                throw new RuntimeException("Respuesta inválida desde ms_cupones");
            }

            log.info("Cupón aplicado correctamente desde ms_cupones");

            return response.getData();

        } catch (Exception e) {
            log.error("Error al conectar con ms_cupones: {}", e.getMessage());
            throw new RuntimeException("No se pudo aplicar el cupón");
        }
    }
}