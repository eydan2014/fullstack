package com.example.user.Repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.user.model.Usuario;
import com.example.user.repository.UsuarioRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class) // 🚀 Usamos Mockito Extension, sin levantar base de datos real
class UserRepositoryTest {

    @Mock
    private UsuarioRepository repository; // 🚀 Mock limpio de la interfaz del repositorio

    @Test
    void debeGuardarUsuarioExitosamente() {
        // 1. GIVEN
        Usuario usuarioInput = new Usuario();
        usuarioInput.setUsername("adminCafeteria");
        usuarioInput.setPassword("encriptada123");
        usuarioInput.setRol("ADMIN");

        Usuario usuarioMocked = new Usuario();
        usuarioMocked.setId(1L); // Le simulamos el ID autogenerado
        usuarioMocked.setUsername("adminCafeteria");
        usuarioMocked.setPassword("encriptada123");
        usuarioMocked.setRol("ADMIN");

        when(repository.save(any(Usuario.class))).thenReturn(usuarioMocked);

        // 2. WHEN
        Usuario guardado = repository.save(usuarioInput);

        // 3. THEN
        assertNotNull(guardado);
        assertNotNull(guardado.getId());
        assertEquals("adminCafeteria", guardado.getUsername());
        verify(repository, times(1)).save(any(Usuario.class));
    }

    @Test
    void debeBuscarPorUsernameExitosamente() {
        // 1. GIVEN
        Usuario usuarioMocked = new Usuario();
        usuarioMocked.setId(2L);
        usuarioMocked.setUsername("clienteFrecuente");
        usuarioMocked.setRol("USER");

        when(repository.findByUsername("clienteFrecuente")).thenReturn(Optional.of(usuarioMocked));

        // 2. WHEN
        Optional<Usuario> resultado = repository.findByUsername("clienteFrecuente");

        // 3. THEN
        assertTrue(resultado.isPresent());
        assertEquals("USER", resultado.get().getRol());
        verify(repository, times(1)).findByUsername("clienteFrecuente");
    }

    @Test
    void debeRetornarVacioCuandoUsernameNoExiste() {
        // 1. GIVEN
        when(repository.findByUsername("usuarioFantasma")).thenReturn(Optional.empty());

        // 2. WHEN
        Optional<Usuario> resultado = repository.findByUsername("usuarioFantasma");

        // 3. THEN
        assertFalse(resultado.isPresent());
        verify(repository, times(1)).findByUsername("usuarioFantasma");
    }
}