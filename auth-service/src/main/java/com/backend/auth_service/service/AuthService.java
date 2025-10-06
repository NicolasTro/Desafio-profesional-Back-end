package com.backend.auth_service.service;

import com.backend.auth_service.exception.ResourceNotFoundException;
import com.backend.auth_service.exception.ValidationException;
import com.backend.auth_service.model.domain.UserCredentials;
import com.backend.auth_service.model.dto.LoginRequest;
import com.backend.auth_service.model.dto.LoginResponse;
import com.backend.auth_service.repository.UserCredentialsRepository;
import com.backend.auth_service.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@Log4j2
@RequiredArgsConstructor
public class AuthService {

    private final UserCredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * Login: valida credenciales y devuelve un JWT real.
     */
    public LoginResponse login(LoginRequest request) {
        try {
            log.info("Intentando login para email: {}", request.getEmail());

            // 🟡 1️⃣ Buscar credenciales por email
            var creds = credentialsRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario inexistente"));

            // 🔴 2️⃣ Validar contraseña
            if (!passwordEncoder.matches(request.getPassword(), creds.getPassword())) {
                log.warn("Intento fallido de login: contraseña incorrecta para {}", request.getEmail());
                throw new ValidationException("Contraseña incorrecta");
            }

            // 🟢 3️⃣ Generar claims del token
            Map<String, Object> claims = Map.of(
                    "email", creds.getEmail(),
                    "userId", creds.getUserId()
            );

            // 🔑 4️⃣ Generar JWT
            String token = jwtUtil.generateToken(creds.getUserId(), claims);

            log.info("✅ Login exitoso para usuario: {}", creds.getUserId());

            return LoginResponse.builder()
                    .token(token)
                    .build();

        } catch (ResourceNotFoundException | ValidationException e) {
            // ⚠️ Errores esperados → los propagamos tal cual
            throw e;
        } catch (Exception e) {
            // ❌ Errores inesperados → 500 Internal Server Error
            log.error("Error interno durante el login para {}: {}", request.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Error interno al procesar el login", e);
        }
    }

    /**
     * Logout: placeholder (si querés invalidar tokens en el futuro).
     */
    public void logout(String token) {
        log.info("Logout recibido. Token: {}", token);
        // Opcional: implementar invalidación (lista negra, expiración, etc.)
    }
}
