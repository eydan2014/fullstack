package com.example.aviso.repository;



import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.example.aviso.model.AvisoModel;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test") // 🚀 Usa la base de datos H2 en memoria configurada para los tests
public class AvisoRepositoryTest {

    @Autowired
    private AvisoRepository avisoRepository;

    @Test
    void debeGuardarYBuscarAvisoExitosamente() {
        // 1. GIVEN (Preparar la entidad con los datos clave del modelo)
        AvisoModel aviso = new AvisoModel();
        aviso.setUsuario("user-789");
        aviso.setMensaje("¡Has acumulado nuevos puntos de fidelidad!");
        aviso.setTipo("FIDELIDAD");
        aviso.setFechaCreacion(LocalDateTime.now());

        // 2. WHEN (Persistir el aviso en la base de datos en memoria)
        AvisoModel avisoGuardado = avisoRepository.save(aviso);

        // 3. THEN (Verificar que se generó el ID y los datos se guardaron correctamente)
        assertNotNull(avisoGuardado.getId(), "El ID no debería ser nulo tras el save");
        
        // Recuperar de la base de datos para validar la consistencia
        Optional<AvisoModel> avisoEncontrado = avisoRepository.findById(avisoGuardado.getId());
        
        assertTrue(avisoEncontrado.isPresent(), "El aviso debería existir en la base de datos");
        assertEquals("user-789", avisoEncontrado.get().getUsuario());
        assertEquals("FIDELIDAD", avisoEncontrado.get().getTipo());
        assertEquals("¡Has acumulado nuevos puntos de fidelidad!", avisoEncontrado.get().getMensaje());
    }
}