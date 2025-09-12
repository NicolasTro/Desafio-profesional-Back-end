package com.backend.auth_service.controller;

import com.backend.auth_service.exception.UnauthorizedException;
import com.backend.auth_service.model.dto.LoginRequest;
import com.backend.auth_service.model.dto.LoginResponse;
import com.backend.auth_service.service.AuthService;
import com.backend.auth_service.util.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticación", description = "Endpoints de login y logout")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida credenciales y devuelve un token JWT")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        if (token == null) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/logout")
    @Operation(summary = "Cerrar sesión", description = "Recibe un JWT y confirma la desconexión")
    public ResponseEntity<LoginResponse> logout(@RequestHeader("Authorization") String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Token inválido o ausente");
        }

        String userEmail;
        try {
            userEmail = jwtUtil.getSubjectFromToken(token);
        } catch (Exception e) {
            throw new UnauthorizedException("Token inválido o expirado");
        }

        return ResponseEntity.ok(new LoginResponse("Logout exitoso para " + userEmail));
    }
}
