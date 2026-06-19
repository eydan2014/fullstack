package com.example.menu;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles; // ◄ Asegúrate de agregar este import

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test") // 🚀 SOLUCIÓN: Utiliza H2 en memoria y salta la conexión real a MySQL
class MenuApplicationTests {

	@Test
	void contextLoads() {
	}

}