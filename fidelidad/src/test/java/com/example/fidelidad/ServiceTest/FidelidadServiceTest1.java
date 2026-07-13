package com.example.fidelidad.ServiceTest;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;


import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import com.example.fidelidad.model.Fidelidad;
import com.example.fidelidad.repository.FidelidadRepository;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@ExtendWith(MockitoExtension.class)
@DataJpaTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.flyway.enabled=false"
})
@ActiveProfiles("test")
class FidelidadRepositoryTest1 {

    @Autowired
    private FidelidadRepository repository;

    @Test
    void debeGuardarRegistroDeFidelidad() {
        // 1. GIVEN
        Fidelidad fidelidad1 = new Fidelidad();
        fidelidad1.setUsuario("usuario-nuevo");
        fidelidad1.setPuntosTotales(500);

        // 2. WHEN
        Fidelidad guardado = repository.save(fidelidad1);

        // 3. THEN
        assertNotNull(guardado.getId(), "El ID autoincremental debería generarse al guardar");
        assertEquals("usuario-nuevo", guardado.getUsuario());
        assertEquals(500, guardado.getPuntosTotales());
    }

    @Test
    void debeBuscarPorUsuarioExitosamente() {
        // 1. GIVEN: Guardamos un registro previo
        Fidelidad fidelidad = new Fidelidad();
        fidelidad.setUsuario("usuario-cafeteria");
        fidelidad.setPuntosTotales(1200);
        repository.save(fidelidad);

        // 2. WHEN: Buscamos usando el método personalizado ocupado en el Service
        Optional<Fidelidad> resultado = repository.findByUsuario("usuario-cafeteria");

        // 3. THEN
        assertTrue(resultado.isPresent(), "El registro debería encontrarse para el usuario indicado");
        assertEquals(1200, resultado.get().getPuntosTotales());
    }

    @Test
    void debeRetornarVacioCuandoUsuarioNoExiste() {
        // 1. GIVEN & WHEN: Buscamos un usuario fantasma que no está en la BD
        Optional<Fidelidad> resultado = repository.findByUsuario("usuario-inexistente");

        // 3. THEN
        assertFalse(resultado.isPresent(), "Debería retornar un Optional vacío");
    }
}