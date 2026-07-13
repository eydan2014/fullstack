package com.example.resena;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource; 

@SpringBootTest
@ActiveProfiles("test")

@TestPropertySource(locations = "classpath:aplication-test.properties") 
class ResenaApplicationTests {

    @Test
    void contextLoads() {
        // Prueba de humo base
    }
}