package com.backend.auth_service.service;

import com.backend.auth_service.client.UsersClient;
import com.backend.auth_service.exception.ResourceNotFoundException;
import com.backend.auth_service.exception.UnauthorizedException;
import com.backend.auth_service.model.domain.User;
import com.backend.auth_service.model.dto.LoginRequest;
import com.backend.auth_service.model.dto.RegisterRequest;
import com.backend.auth_service.model.dto.RegisterResponse;
import com.backend.auth_service.model.dto.UserProfileRequest;
import com.backend.auth_service.repository.UserRepository;
import com.backend.auth_service.util.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final UsersClient usersClient;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       UsersClient usersClient) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.usersClient = usersClient;
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }

        return jwtUtil.generateToken(user.getEmail());
    }

    public RegisterResponse register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        // Guardar credenciales en Auth
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userRepository.saveAndFlush(user);

        // Crear perfil en Users y obtener alias + CVU
        RegisterResponse response;
        try {
            UserProfileRequest profile = new UserProfileRequest();
            profile.setUserId(user.getId().toString());
            profile.setNombre(request.getNombre());
            profile.setApellido(request.getApellido());
            profile.setDni(request.getDni());
            profile.setEmail(request.getEmail());
            profile.setTelefono(request.getTelefono());

            response = usersClient.createUserProfile(profile);
        } catch (Exception e) {
            System.err.println(" No se pudo crear perfil en Users-service: " + e.getMessage());

            // Si falla Users, devolvemos al menos credenciales básicas
            response = new RegisterResponse();
            response.setId(user.getId().toString());
            response.setEmail(user.getEmail());
        }

        return response;
    }


}
