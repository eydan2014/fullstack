package com.example.aviso;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; // ◄ IMPORTANTE

@SpringBootTest
@ActiveProfiles("test") // 🚀 Fuerza al test de contexto a usar la base de datos H2 en memoria
class AvisoApplicationTests {

    @Test
    void contextLoads() {
        // Prueba de humo base para verificar que el contexto de Spring levanta correctamente
    }
}