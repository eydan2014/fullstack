package com.example.user.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.user.repository.RefreshTokenRepository;
import com.example.user.repository.UsuarioRepository;
import com.example.user.security.JwtUtil;
import com.example.user.dto.UsuarioResponse;
import com.example.user.dto.RegisterRequest;
import com.example.user.dto.LoginRequest;
import com.example.user.model.RefreshToken;
import com.example.user.model.Usuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j 
public class UsuarioService {

    private final UsuarioRepository usuarioRepo;
    private final RefreshTokenRepository refreshTokenRepo;
    private final PasswordEncoder encoder;          
    private final AuthenticationManager authManager;
    private final JwtUtil jwtUtil;                  

    public UsuarioResponse register(RegisterRequest req) {
        log.info("[NEGOCIO] Registrando nuevo usuario en base de datos: {}", req.getUsername());

        Usuario user = new Usuario();
        user.setUsername(req.getUsername());
        user.setPassword(encoder.encode(req.getPassword()));
        
        // 🚀 REGLA DE ORO: Guardamos solo "USER" (Limpio). 
        // De esta manera evitamos que se duplique como 'ROLE_ROLE_USER' cuando el JwtUtil genere el token.
        user.setRol("USER"); 

        usuarioRepo.save(user);

        String access = jwtUtil.generarToken(user.getUsername(), user.getRol());
        String refresh = registrarYObtenerRefreshToken(user.getUsername()); 

        return new UsuarioResponse(access, refresh);
    }

    public UsuarioResponse login(LoginRequest req) {
        log.info("[NEGOCIO] Autenticando usuario: {}", req.getUsername());

        authManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
        );

        Usuario user = usuarioRepo.findByUsername(req.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        String access = jwtUtil.generarToken(user.getUsername(), user.getRol());
        String refresh = registrarYObtenerRefreshToken(user.getUsername()); 

        return new UsuarioResponse(access, refresh);
    }

    public UsuarioResponse refresh(String refreshToken) {
        log.info("[NEGOCIO] Intentando validar y renovar mediante Refresh Token");

        if (!jwtUtil.esValido(refreshToken) || !jwtUtil.esRefreshToken(refreshToken)) {
            log.error("[ERROR] El Refresh Token enviado posee una firma inválida o expirada.");
            throw new IllegalArgumentException("Refresh token inválido o expirado");
        }

        RefreshToken token = refreshTokenRepo.findByToken(refreshToken)
                .orElseThrow(() -> new IllegalArgumentException("Refresh token no registrado en la base de datos"));

        Usuario user = usuarioRepo.findByUsername(token.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Usuario dueño del token no existe"));

        String newAccess = jwtUtil.generarToken(user.getUsername(), user.getRol());

        return new UsuarioResponse(newAccess, refreshToken);
    }

    private String registrarYObtenerRefreshToken(String username) {
        String tokenFirmado = jwtUtil.generarRefreshToken(username); 

        RefreshToken rt = new RefreshToken();
        rt.setToken(tokenFirmado);
        rt.setUsername(username);
        rt.setExpiryDate(new java.sql.Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)); 

        refreshTokenRepo.save(rt);
        return tokenFirmado;
    }

    public boolean existeUsuario(String username) {
        log.info("[INTER-SERVICIO] Validando existencia remota del usuario: {}", username);
        return usuarioRepo.existsByUsername(username);
    }
}