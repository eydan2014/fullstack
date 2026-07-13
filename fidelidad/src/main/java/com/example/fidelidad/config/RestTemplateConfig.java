package com.example.fidelidad.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate balanceado por Eureka: permite invocar a otros microservicios
 * por su nombre lógico registrado (spring.application.name), por ejemplo
 * "http://user/..." en vez de una URL fija tipo "http://localhost:8082/...".
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
