package com.backend.auth_service.service;

import com.backend.auth_service.exception.ResourceNotFoundException;
import com.backend.auth_service.exception.UnauthorizedException;
import com.backend.auth_service.model.dto.LoginRequest;
import com.backend.auth_service.model.domain.User;
import com.backend.auth_service.repository.UserRepository;
import com.backend.auth_service.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        return jwtUtil.generateToken(user.getEmail());
    }
}
