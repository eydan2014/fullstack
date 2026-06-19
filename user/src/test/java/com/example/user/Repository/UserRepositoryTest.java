package com.example.user.Repository;

import com.example.user.model.Usuario;
import com.example.user.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UsuarioRepository repository;

    @Test
    void debeGuardarUsuarioExitosamente() {
        Usuario usuario = new Usuario();
        usuario.setUsername("adminCafeteria");
        usuario.setPassword("encriptada123");
        usuario.setRol("ADMIN");

        Usuario guardado = repository.save(usuario);

        assertNotNull(guardado.getId());
        assertEquals("adminCafeteria", guardado.getUsername());
    }

    @Test
    void debeBuscarPorUsernameExitosamente() {
        Usuario usuario = new Usuario();
        usuario.setUsername("clienteFrecuente");
        usuario.setPassword("passwordSecret");
        usuario.setRol("USER");
        repository.save(usuario);

        Optional<Usuario> resultado = repository.findByUsername("clienteFrecuente");

        assertTrue(resultado.isPresent());
        assertEquals("USER", resultado.get().getRol());
    }

    @Test
    void debeRetornarVacioCuandoUsernameNoExiste() {
        Optional<Usuario> resultado = repository.findByUsername("usuarioFantasma");
        assertFalse(resultado.isPresent());
    }
}