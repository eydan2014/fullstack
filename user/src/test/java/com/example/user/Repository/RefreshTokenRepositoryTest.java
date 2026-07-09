package com.example.user.Repository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.user.model.RefreshToken;
import com.example.user.repository.RefreshTokenRepository;

import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) // 🚀 Mockito puro, sin bases de datos inestables
class RefreshTokenRepositoryTest {

    @Mock
    private RefreshTokenRepository repository;

    @Test
    void debeGuardarRefreshTokenExitosamente() {
        // 1. GIVEN
        RefreshToken tokenInput = new RefreshToken();
        tokenInput.setToken("uuid-token-de-prueba-1234");
        tokenInput.setUsername("usuario.test"); 
        tokenInput.setExpiryDate(new Date());

        RefreshToken tokenMocked = new RefreshToken();
        tokenMocked.setId(1L); // Simulamos el ID autogenerado
        tokenMocked.setToken("uuid-token-de-prueba-1234");
        tokenMocked.setUsername("usuario.test");
        tokenMocked.setExpiryDate(tokenInput.getExpiryDate());

        when(repository.save(any(RefreshToken.class))).thenReturn(tokenMocked);

        // 2. WHEN
        RefreshToken guardado = repository.save(tokenInput);

        // 3. THEN
        assertNotNull(guardado);
        assertNotNull(guardado.getId());
        assertEquals("uuid-token-de-prueba-1234", guardado.getToken());
        verify(repository, times(1)).save(any(RefreshToken.class));
    }

    @Test
    void debeBuscarRefreshTokenPorSuTokenCriptografico() {
        // 1. GIVEN
        RefreshToken tokenMocked = new RefreshToken();
        tokenMocked.setId(2L);
        tokenMocked.setToken("jwt-refresh-token-valido");
        tokenMocked.setUsername("usuario.test");

        when(repository.findByToken("jwt-refresh-token-valido")).thenReturn(Optional.of(tokenMocked));

        // 2. WHEN
        Optional<RefreshToken> resultado = repository.findByToken("jwt-refresh-token-valido");

        // 3. THEN
        assertTrue(resultado.isPresent());
        assertEquals("jwt-refresh-token-valido", resultado.get().getToken());
        verify(repository, times(1)).findByToken("jwt-refresh-token-valido");
    }

    @Test
    void debeRetornarVacioCuandoElTokenNoExiste() {
        // 1. GIVEN
        when(repository.findByToken("token-expirado-o-inexistente")).thenReturn(Optional.empty());

        // 2. WHEN
        Optional<RefreshToken> resultado = repository.findByToken("token-expirado-o-inexistente");

        // 3. THEN
        assertFalse(resultado.isPresent());
        verify(repository, times(1)).findByToken("token-expirado-o-inexistente");
    }
}