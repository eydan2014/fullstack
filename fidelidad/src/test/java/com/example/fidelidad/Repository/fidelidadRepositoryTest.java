package com.example.fidelidad.Repository;

import com.example.fidelidad.model.Fidelidad;
import com.example.fidelidad.repository.FidelidadRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class fidelidadRepositoryTest {

    @Autowired
    private FidelidadRepository repository;

    @Test
    void debeGuardarRegistroDeFidelidad() {
        // 1. GIVEN: Definición de una nueva entidad de fidelidad
        Fidelidad fidelidad = new Fidelidad();
        fidelidad.setUsuario("usuario-nuevo");
        fidelidad.setPuntosTotales(500);

        // 2. WHEN: Persistencia en la base de datos de pruebas (H2 / en memoria)
        Fidelidad guardado = repository.save(fidelidad);

        // 3. THEN: Verificación de la correcta inserción del registro
        assertNotNull(guardado.getId(), "El ID autoincremental debería generarse al guardar");
        assertEquals("usuario-nuevo", guardado.getUsuario());
        assertEquals(500, guardado.getPuntosTotales());
    }

    @Test
    void debeBuscarPorUsuarioExitosamente() {
        // 1. GIVEN: Inserción previa de un registro controlado
        Fidelidad fidelidad = new Fidelidad();
        fidelidad.setUsuario("usuario-cafeteria");
        fidelidad.setPuntosTotales(1200);
        repository.save(fidelidad);

        // 2. WHEN: Ejecución de tu método de consulta personalizado
        Optional<Fidelidad> resultado = repository.findByUsuario("usuario-cafeteria");

        // 3. THEN: Comprobamos que el Optional contenga el registro esperado
        assertTrue(resultado.isPresent(), "El registro debería encontrarse para el usuario indicado");
        assertEquals(1200, resultado.get().getPuntosTotales());
    }

    @Test
    void debeRetornarVacioCuandoUsuarioNoExiste() {
        // 1. GIVEN & WHEN: Consultamos un usuario que no ha sido insertado
        Optional<Fidelidad> resultado = repository.findByUsuario("usuario-inexistente");

        // 3. THEN: Comprobamos que devuelva de forma segura un Optional.empty()
        assertFalse(resultado.isPresent(), "Debería retornar un Optional vacío");
    }
}