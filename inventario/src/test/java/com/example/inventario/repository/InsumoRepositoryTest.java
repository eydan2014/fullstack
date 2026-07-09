package com.example.inventario.repository;


import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.inventario.model.Insumo;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
public class InsumoRepositoryTest {

    @Autowired
    private InsumoRepository insumoRepository;

    @Test
    void debeGuardarYBuscarInsumoPorNombre() {
        // 1. GIVEN
        Insumo insumo = new Insumo();
        insumo.setNombre("Café en Grano");
        insumo.setStock(120);

        // 2. WHEN
        Insumo insumoGuardado = insumoRepository.save(insumo);

        // 3. THEN
        assertNotNull(insumoGuardado.getIdInsumo());
        
        Optional<Insumo> encontrado = insumoRepository.findByNombre("Café en Grano");
        assertTrue(encontrado.isPresent());
        assertEquals(120, encontrado.get().getStock());
    }
}