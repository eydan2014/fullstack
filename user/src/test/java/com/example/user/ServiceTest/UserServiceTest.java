package com.example.user.ServiceTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.user.dto.LoginRequest;
import com.example.user.dto.RegisterRequest;
import com.example.user.dto.UsuarioResponse;
import com.example.user.model.RefreshToken;
import com.example.user.model.Usuario;
import com.example.user.repository.RefreshTokenRepository;
import com.example.user.repository.UsuarioRepository;
import com.example.user.security.JwtUtil;
import com.example.user.service.UsuarioService;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UsuarioRepository usuarioRepo;

    @Mock
    private RefreshTokenRepository refreshTokenRepo;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private AuthenticationManager authManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UsuarioService service;

    // Test 1 - Registrar un nuevo usuario exitosamente
    @Test
    void deberiaRegistrarUsuarioExitosamente() {
        // 1. GIVEN
        RegisterRequest req = new RegisterRequest();
        req.setUsername("leo.martinez");
        req.setPassword("password123");

        when(encoder.encode("password123")).thenReturn("passwordEncriptado");
        when(jwtUtil.generarToken("leo.martinez", "USER")).thenReturn("mock-access-token");
        when(jwtUtil.generarRefreshToken("leo.martinez")).thenReturn("mock-refresh-token");
        when(usuarioRepo.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(refreshTokenRepo.save(any(RefreshToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 2. WHEN
        UsuarioResponse response = service.register(req);

        // 3. THEN
        assertNotNull(response);
        assertEquals("mock-access-token", response.getAccessToken());
        assertEquals("mock-refresh-token", response.getRefreshToken());
        
        verify(encoder).encode("password123");
        verify(usuarioRepo).save(any(Usuario.class));
        verify(refreshTokenRepo).save(any(RefreshToken.class));
    }

    // Test 2 - Iniciar sesión exitosamente
    @Test
    void deberiaHacerLoginExitosamente() {
        // 1. GIVEN
        LoginRequest req = new LoginRequest();
        req.setUsername("leo.martinez");
        req.setPassword("password123");

        Usuario usuarioExistente = new Usuario();
        usuarioExistente.setUsername("leo.martinez");
        usuarioExistente.setRol("USER");

        // Simulamos que la autenticación del manager pasa sin lanzar BadCredentialsException
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        when(usuarioRepo.findByUsername("leo.martinez")).thenReturn(Optional.of(usuarioExistente));
        when(jwtUtil.generarToken("leo.martinez", "USER")).thenReturn("mock-access-token");
        when(jwtUtil.generarRefreshToken("leo.martinez")).thenReturn("mock-refresh-token");

        // 2. WHEN
        UsuarioResponse response = service.login(req);

        // 3. THEN
        assertNotNull(response);
        assertEquals("mock-access-token", response.getAccessToken());
        assertEquals("mock-refresh-token", response.getRefreshToken());

        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(usuarioRepo).findByUsername("leo.martinez");
    }

    // Test 3 - Login falla cuando el usuario no se encuentra en BD
    @Test
    void deberiaLanzarExcepcionCuandoLoginUsuarioNoExiste() {
        // 1. GIVEN
        LoginRequest req = new LoginRequest();
        req.setUsername("fantasma");
        req.setPassword("password123");

        when(usuarioRepo.findByUsername("fantasma")).thenReturn(Optional.empty());

        // 2. WHEN & 3. THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.login(req));
        assertEquals("Usuario no encontrado", ex.getMessage());
    }

    // Test 4 - Renovar fichas mediante un Refresh Token Válido
    @Test
    void deberiaRenovarTokensConRefreshTokenValido() {
        // 1. GIVEN
        String refreshTokenEnviado = "mock-refresh-token-valido";
        
        RefreshToken rtExistente = new RefreshToken();
        rtExistente.setToken(refreshTokenEnviado);
        rtExistente.setUsername("leo.martinez");

        Usuario usuarioOwner = new Usuario();
        usuarioOwner.setUsername("leo.martinez");
        usuarioOwner.setRol("USER");

        when(jwtUtil.esValido(refreshTokenEnviado)).thenReturn(true);
        when(jwtUtil.esRefreshToken(refreshTokenEnviado)).thenReturn(true);
        when(refreshTokenRepo.findByToken(refreshTokenEnviado)).thenReturn(Optional.of(rtExistente));
        when(usuarioRepo.findByUsername("leo.martinez")).thenReturn(Optional.of(usuarioOwner));
        when(jwtUtil.generarToken("leo.martinez", "USER")).thenReturn("nuevo-access-token");

        // 2. WHEN
        UsuarioResponse response = service.refresh(refreshTokenEnviado);

        // 3. THEN
        assertNotNull(response);
        assertEquals("nuevo-access-token", response.getAccessToken());
        assertEquals(refreshTokenEnviado, response.getRefreshToken());
    }

    // Test 5 - Renovación falla si el token es estructuralmente inválido o expiró
    @Test
    void deberiaLanzarExcepcionSiRefreshTokenEsInvalido() {
        // 1. GIVEN
        String tokenInvalido = "token-roto-o-expirado";
        when(jwtUtil.esValido(tokenInvalido)).thenReturn(false);

        // 2. WHEN & 3. THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.refresh(tokenInvalido));
        assertEquals("Refresh token inválido o expirado", ex.getMessage());
    }

    // Test 6 - Renovación falla si el token es válido estructuralmente pero no está guardado en BD
    @Test
    void deberiaLanzarExcepcionSiRefreshTokenNoEstaEnBD() {
        // 1. GIVEN
        String tokenNoRegistrado = "token-valido-pero-inexistente-en-bd";
        when(jwtUtil.esValido(tokenNoRegistrado)).thenReturn(true);
        when(jwtUtil.esRefreshToken(tokenNoRegistrado)).thenReturn(true);
        when(refreshTokenRepo.findByToken(tokenNoRegistrado)).thenReturn(Optional.empty());

        // 2. WHEN & 3. THEN
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> service.refresh(tokenNoRegistrado));
        assertEquals("Refresh token no registrado en la base de datos", ex.getMessage());
    }
}