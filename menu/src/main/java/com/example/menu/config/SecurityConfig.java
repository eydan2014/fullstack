package com.example.menu.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.example.menu.security.JwtFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // ◄ OBLIGATORIO: Habilita los @PreAuthorize("hasAuthority(...)") de tu controlador
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;

   @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            // 🌐 Configuración de CORS para permitir peticiones desde cualquier origen/frontend
            .cors(cors -> cors.configurationSource(request -> {
                var corsConfiguration = new org.springframework.web.cors.CorsConfiguration();
                corsConfiguration.setAllowedOrigins(List.of("*")); // Ajustar según las URL de tu frontend
                corsConfiguration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
                corsConfiguration.setAllowedHeaders(List.of("*"));
                return corsConfiguration;
            }))
            // 🛡️ Deshabilitamos CSRF ya que usamos tokens JWT (Stateless)
            .csrf(csrf -> csrf.disable())
            // 🔄 Definimos la política de sesiones como SIN ESTADO
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 🔓 Permitimos la consulta rápida de precios para que el módulo de Pagos no rebote
                .requestMatchers("/api/productos/*/precio").permitAll()
                // 🔐 El resto de la API requiere token
                .anyRequest().authenticated()
            )
            // 🚀 Inyectamos tu filtro corregido antes del filtro de Spring
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
            .build();
    }
}