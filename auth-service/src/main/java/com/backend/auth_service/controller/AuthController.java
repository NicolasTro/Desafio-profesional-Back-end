package com.backend.auth_service.controller;

import com.backend.auth_service.model.dto.LoginRequest;
import com.backend.auth_service.model.dto.LoginResponse;
import com.backend.auth_service.service.AuthService;
import com.backend.auth_service.util.JwtUtil;
import io.jsonwebtoken.JwtException;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final JwtUtil jwtUtil;

    public AuthController(AuthService authService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        String token = authService.login(request);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            @RequestHeader(value = "Authorization", required = false) String token) {

        if (token == null || token.isBlank()) {
            throw new JwtException("Token ausente o vacío");
        }

        String userEmail = jwtUtil.getSubjectFromToken(token);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout exitoso para " + userEmail);

        return ResponseEntity.ok(response);
    }


}
