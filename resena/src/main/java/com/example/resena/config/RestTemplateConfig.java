package com.example.resena.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate balanceado por Eureka: permite invocar a otros microservicios
 * por su nombre lógico registrado (spring.application.name), por ejemplo
 * "http://menu/..." o "http://user/..." en vez de URLs fijas tipo
 * "http://localhost:8083/...", que solo funcionan en local y rompen en Docker.
 */
@Configuration
public class RestTemplateConfig {

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
