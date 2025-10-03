package com.backend.auth_service.service;

import com.backend.auth_service.client.AccountsClient;
import com.backend.auth_service.client.UsersClient;
import com.backend.auth_service.model.domain.UserCredentials;
import com.backend.auth_service.model.dto.*;
import com.backend.auth_service.repository.UserCredentialsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class RegistrationSagaService {

    private final UserCredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsersClient usersClient;
    private final AccountsClient accountsClient;

    public RegisterResponse register(RegisterRequest request) {
        String credentialId = null;
        String userId = UUID.randomUUID().toString(); // 👈 generar el userId global
        String accountId = null;

        try {
            // 1. Guardar credenciales en auth-service
            UserCredentials creds = UserCredentials.builder()
                    .id(UUID.randomUUID().toString())              // id propio de credenciales
                    .userId(userId)                                // 👈 setear el userId
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();

            credentialId = credentialsRepository.save(creds).getId();
            log.info("Credenciales creadas con id: {} para userId: {}", credentialId, userId);

            // 2. Crear usuario en users-service
            UserProfileRequest userProfile = new UserProfileRequest();
            userProfile.setUserId(userId);
            userProfile.setNombre(request.getNombre());
            userProfile.setApellido(request.getApellido());
            userProfile.setDni(request.getDni());
            userProfile.setEmail(request.getEmail());
            userProfile.setTelefono(request.getTelefono());

            usersClient.createUser(userProfile);
            log.info("Usuario creado en users-service con id: {}", userId);

            // 3. Crear cuenta en accounts-service
            AccountCreateDTO accountDto = new AccountCreateDTO();
            accountDto.setUserId(userId);

            AccountResponseDTO account = accountsClient.createAccount(accountDto);
            accountId = account.getId();
            log.info("Cuenta creada en accounts-service con id: {} y cvu: {}", accountId, account.getCvu());

            // 4. Devolver respuesta al frontend
            return new RegisterResponse(
                    userId,
                    request.getEmail(),
                    request.getNombre(),
                    request.getApellido(),
                    account.getCvu(),
                    account.getAlias()
            );

        } catch (Exception e) {
            log.error("Error en Saga, aplicando rollback", e);

            // rollback
            if (accountId != null) {
                try { accountsClient.deleteAccount(accountId); }
                catch (Exception ex) { log.error("Rollback cuenta falló", ex); }
            }
            if (userId != null) {
                try { usersClient.deleteUser(userId); }
                catch (Exception ex) { log.error("Rollback usuario falló", ex); }
            }
            if (credentialId != null) {
                try { credentialsRepository.deleteById(credentialId); }
                catch (Exception ex) { log.error("Rollback credenciales falló", ex); }
            }

            throw new RuntimeException("Fallo en el registro, rollback ejecutado", e);
        }
    }
}

