package com.backend.auth_service.controller;

import com.backend.auth_service.model.dto.LoginRequest;
import com.backend.auth_service.model.dto.LoginResponse;
import com.backend.auth_service.service.AuthService;
import com.backend.auth_service.util.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;




    @Autowired
    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            String token = authService.login(request);
            return ResponseEntity.ok(new LoginResponse(token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new LoginResponse("Credenciales inválidas"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<LoginResponse> logout(@RequestHeader("Authorization") String token) {
        try {
            if (token == null || token.isBlank()) {
                return ResponseEntity.status(400).body(new LoginResponse("Token inválido o ausente"));
            }


            String userEmail = jwtUtil.getSubjectFromToken(token);

            return ResponseEntity.ok(new LoginResponse("Logout exitoso para " + userEmail));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(new LoginResponse("Token inválido"));
        }
    }

}

