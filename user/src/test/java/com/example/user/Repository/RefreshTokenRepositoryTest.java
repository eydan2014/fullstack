package com.example.user.Repository;

import com.example.user.model.RefreshToken;
import com.example.user.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class RefreshTokenRepositoryTest {

    @Autowired
    private RefreshTokenRepository repository;

    @Test
    void debeGuardarRefreshTokenExitosamente() {
        RefreshToken token = new RefreshToken();
        token.setToken("uuid-token-de-prueba-1234");
        token.setUsername("usuario.test"); 
        token.setExpiryDate(new Date());

        RefreshToken guardado = repository.save(token);

        assertNotNull(guardado.getId());
        assertEquals("uuid-token-de-prueba-1234", guardado.getToken());
    }

    @Test
    void debeBuscarRefreshTokenPorSuTokenCriptografico() {
        RefreshToken token = new RefreshToken();
        token.setToken("jwt-refresh-token-valido");
        token.setUsername("usuario.test"); 
        token.setExpiryDate(new Date());
        repository.save(token);

        Optional<RefreshToken> resultado = repository.findByToken("jwt-refresh-token-valido");

        assertTrue(resultado.isPresent());
        assertEquals("jwt-refresh-token-valido", resultado.get().getToken());
    }

    @Test
    void debeRetornarVacioCuandoElTokenNoExiste() {
        Optional<RefreshToken> resultado = repository.findByToken("token-expirado-o-inexistente");
        assertFalse(resultado.isPresent());
    }
}