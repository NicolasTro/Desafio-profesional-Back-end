package com.backend.auth_service.service;

import com.backend.auth_service.client.UsersClient;
import com.backend.auth_service.client.AccountsClient;
import com.backend.auth_service.model.dto.*;
import com.backend.auth_service.model.domain.UserCredentials;
import com.backend.auth_service.repository.UserCredentialsRepository;


import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Log4j2
public class AuthService {

    private final UserCredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsersClient usersClient;
    private final AccountsClient accountsClient;

    public AuthService(UserCredentialsRepository credentialsRepository,
                       PasswordEncoder passwordEncoder,
                       UsersClient usersClient,
                       AccountsClient accountsClient) {
        this.credentialsRepository = credentialsRepository;
        this.passwordEncoder = passwordEncoder;
        this.usersClient = usersClient;
        this.accountsClient = accountsClient;
    }

    /**
     * Registro completo:
     * 1. Credenciales en auth-service
     * 2. Perfil en users-service
     * 3. Cuenta en accounts-service
     */
    public RegisterResponse register(RegisterRequest request) {
        // 1. Generar un userId único
        String userId = UUID.randomUUID().toString();

        // 2. Guardar credenciales en auth-service
        UserCredentials creds = new UserCredentials();
        creds.setUserId(userId);
        creds.setEmail(request.getEmail());
        creds.setPassword(passwordEncoder.encode(request.getPassword()));
//        creds.setRole("USER");
        credentialsRepository.save(creds);

        // 3. Crear perfil en users-service
        UserProfileRequest userProfile = new UserProfileRequest();
        userProfile.setUserId(userId);
        userProfile.setNombre(request.getNombre());
        userProfile.setApellido(request.getApellido());
        userProfile.setDni(request.getDni());
        userProfile.setEmail(request.getEmail());
        userProfile.setTelefono(request.getTelefono());

        log.info("Registrando usuario en users-service: " + userProfile);

        usersClient.createUser(userProfile);

        // 4. Crear cuenta en accounts-service
        AccountCreateDTO accountDto = new AccountCreateDTO();
        accountDto.setUserId(userId);
        AccountResponseDTO account = accountsClient.createAccount(accountDto);

        // 5. Armar respuesta unificada para el frontend
        RegisterResponse response = new RegisterResponse();
        response.setUserId(userId);
        response.setEmail(request.getEmail());
        response.setNombre(request.getNombre());
        response.setApellido(request.getApellido());
        response.setCvu(account.getCvu());
        response.setAlias(account.getAlias());

        return response;
    }

    /**
     * Login: valida credenciales y devuelve JWT (simulado por ahora)
     */
    public LoginResponse login(LoginRequest request) {
        UserCredentials creds = credentialsRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(request.getPassword(), creds.getPassword())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        // 🔑 Generación de JWT (por ahora simulado)
        String token = "fake-jwt-token-for-" + creds.getUserId();

        LoginResponse response = new LoginResponse();
//        response.setUserId(creds.getUserId());
//        response.setEmail(creds.getEmail());
        response.setToken(token);

        return response;
    }




}
