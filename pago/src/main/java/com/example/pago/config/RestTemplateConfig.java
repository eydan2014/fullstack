package com.example.pago.config;


import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.builder()
                .build();
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder()
                .build();
    }

    // 🔗 RestTemplate balanceado por Eureka: permite invocar a otros microservicios
    // por su nombre lógico registrado (spring.application.name), por ejemplo
    // "http://menu/...", "http://fidelidad/..." o "http://aviso/..." en vez de
    // URLs fijas tipo "http://localhost:8083/...", que solo funcionan en local.
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
