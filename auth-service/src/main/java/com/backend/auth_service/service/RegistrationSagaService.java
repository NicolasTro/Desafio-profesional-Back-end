package com.backend.auth_service.service;

import com.backend.auth_service.client.AccountsClient;
import com.backend.auth_service.client.UsersClient;
import com.backend.auth_service.exception.ValidationException;
import com.backend.auth_service.model.domain.UserCredentials;
import com.backend.auth_service.model.dto.*;
import com.backend.auth_service.repository.UserCredentialsRepository;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Log4j2
public class RegistrationSagaService {

    private final UserCredentialsRepository credentialsRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsersClient usersClient;
    private final AccountsClient accountsClient;

    /**
     * Orquesta el proceso completo de registro de usuario:
     * 1. Verifica si el email ya existe (auth-service).
     * 2. Crea credenciales.
     * 3. Crea perfil en users-service.
     * 4. Crea cuenta en accounts-service.
     * 5. Si ocurre un error, aplica rollback secuencial.
     */
    public RegisterResponse register(RegisterRequest request) {
        String credentialId = null;
        String userId = UUID.randomUUID().toString(); // ID global compartido entre servicios
        String accountId = null;

        try {
            log.info("Iniciando proceso de registro para {}", request.getEmail());

            // 🟡 1️⃣ Verificar si ya existe el email en auth-service
            Optional<UserCredentials> existing = credentialsRepository.findByEmail(request.getEmail());
            if (existing.isPresent()) {
                log.warn("Intento de registro con email existente: {}", request.getEmail());
                throw new ValidationException("El email ya está registrado");
            }

            // 🟢 2️⃣ Crear credenciales en auth-service
            UserCredentials creds = UserCredentials.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .email(request.getEmail())
                    .password(passwordEncoder.encode(request.getPassword()))
                    .build();

            credentialId = credentialsRepository.save(creds).getId();
            log.info("Credenciales creadas con id: {} para userId: {}", credentialId, userId);

            // 🟢 3️⃣ Crear perfil en users-service
            UserProfileRequest userProfile = UserProfileRequest.builder()
                    .userId(userId)
                    .nombre(request.getNombre())
                    .apellido(request.getApellido())
                    .dni(request.getDni())
                    .email(request.getEmail())
                    .telefono(request.getTelefono())
                    .build();

            RegisterResponse userResponse = usersClient.createUser(userProfile);
            log.info("Usuario creado en users-service con id: {}", userResponse.getUserId());

            // 🟢 4️⃣ Crear cuenta en accounts-service
            AccountCreateDTO accountDto = new AccountCreateDTO();
            accountDto.setUserId(userId);

            AccountResponseDTO account = accountsClient.createAccount(accountDto);
            accountId = account.getId();
            log.info("Cuenta creada en accounts-service con id: {} y cvu: {}", accountId, account.getCvu());

            // 🟢 5️⃣ Consolidar respuesta
            RegisterResponse finalResponse = RegisterResponse.builder()
                    .userId(userId)
                    .nombre(userResponse.getNombre())
                    .apellido(userResponse.getApellido())
                    .email(userResponse.getEmail())
                    .telefono(userResponse.getTelefono())
                    .cvu(account.getCvu())
                    .alias(account.getAlias())
                    .build();

            log.info("✅ Registro completo exitoso para usuario: {}", userId);
            return finalResponse;

        } catch (FeignException.BadRequest e) {
            // errores 400 devueltos por users o accounts
            String message = extractErrorMessage(e);
            log.warn("Error de validación recibido desde otro servicio: {}", message);

            rollback(accountId, userId, credentialId);
            throw new ValidationException(message);

        } catch (FeignException e) {
            // errores 404, 500 u otros del Feign client
            String message = extractErrorMessage(e);
            log.error("Error Feign en comunicación con otro microservicio: {}", message);

            rollback(accountId, userId, credentialId);
            throw new RuntimeException("Error en comunicación con otro microservicio: " + message, e);

        } catch (ValidationException e) {
            // error local (por ejemplo email duplicado)
            log.warn("Error de validación local: {}", e.getMessage());
            throw e;

        } catch (Exception e) {
            // error general
            log.error("Error general en Saga, aplicando rollback", e);
            rollback(accountId, userId, credentialId);
            throw new RuntimeException("Fallo en el registro. Rollback ejecutado.", e);
        }
    }

    // 🔁 Rollback centralizado
    private void rollback(String accountId, String userId, String credentialId) {
        if (accountId != null) {
            try {
                accountsClient.deleteAccount(accountId);
                log.warn("Rollback: cuenta eliminada ({})", accountId);
            } catch (Exception ex) {
                log.error("Rollback de cuenta falló", ex);
            }
        }

        if (userId != null) {
            try {
                usersClient.deleteUser(userId);
                log.warn("Rollback: usuario eliminado ({})", userId);
            } catch (Exception ex) {
                log.error("Rollback de usuario falló", ex);
            }
        }

        if (credentialId != null) {
            try {
                credentialsRepository.deleteById(credentialId);
                log.warn("Rollback: credenciales eliminadas ({})", credentialId);
            } catch (Exception ex) {
                log.error("Rollback de credenciales falló", ex);
            }
        }
    }

    // 🧩 Utilidad para extraer mensaje de error de un FeignException
    private String extractErrorMessage(FeignException e) {
        try {
            String content = e.contentUTF8();
            if (content == null || content.isBlank()) return e.getMessage();
            if (content.contains("error")) {
                int start = content.indexOf(":") + 2;
                int end = content.lastIndexOf("\"");
                return content.substring(start, end);
            }
            return content;
        } catch (Exception ex) {
            return "Error desconocido en servicio remoto";
        }
    }
}
