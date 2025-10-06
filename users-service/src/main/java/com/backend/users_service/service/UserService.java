package com.backend.users_service.service;

import com.backend.users_service.client.AccountsClient;
import com.backend.users_service.exception.ValidationException;
import com.backend.users_service.model.domain.User;
import com.backend.users_service.model.dto.AccountResponseDTO;
import com.backend.users_service.model.dto.UserProfileRequest;
import com.backend.users_service.model.dto.RegisterResponse;
import com.backend.users_service.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Service
@Log4j2

public class UserService {

    private final UserRepository userRepository;
    private final AccountsClient accountsClient;

    public UserService(UserRepository userRepository, AccountsClient accountsClient) {
        this.userRepository = userRepository;

        this.accountsClient = accountsClient;
    }

    /**
     * Registra un nuevo usuario a partir de la información enviada por auth-service.
     * El userId ya fue generado en auth-service.
     */
    public RegisterResponse register(UserProfileRequest request) {
        if (userRepository.existsById(request.getUserId())) {
            throw new RuntimeException("El usuario con ID " + request.getUserId() + " ya existe");
        }

        if (userRepository.existsByDni(request.getDni())) {
            throw new ValidationException("El DNI  " + request.getDni() + " ya existe");
        }


        log.info("Entro en Register del UserService");
        User user = User.builder()
                .userId(request.getUserId())
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .dni(request.getDni())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .build();

        User saved = userRepository.save(user);
        log.info("Usuario registrado correctamente: {} {}", saved.getNombre(), saved.getApellido());

        return RegisterResponse.builder()
                .userId(saved.getUserId())
                .nombre(saved.getNombre())
                .apellido(saved.getApellido())
                .dni(saved.getDni())
                .email(saved.getEmail())
                .telefono(saved.getTelefono())
                .build();
    }

    public RegisterResponse getUserById(String id) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 🔹 Obtener datos de la cuenta desde accounts-service
        AccountResponseDTO account = accountsClient.getAccountByUserId(user.getUserId());

        return RegisterResponse.builder()
                .userId(user.getUserId())
                .nombre(user.getNombre())
                .apellido(user.getApellido())
                .dni(user.getDni())
                .email(user.getEmail())
                .telefono(user.getTelefono())
                .cvu(account.getCvu())
                .alias(account.getAlias())
                .build();
    }

    /**
     * Actualiza la información básica del usuario.
     */
    public RegisterResponse updateUser(String id, UserProfileRequest request) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));

        existing.setNombre(request.getNombre());
        existing.setApellido(request.getApellido());
        existing.setDni(request.getDni());
        existing.setEmail(request.getEmail());
        existing.setTelefono(request.getTelefono());

        User updated = userRepository.save(existing);
        log.info("Usuario actualizado: {} {}", updated.getNombre(), updated.getApellido());

        return RegisterResponse.builder()
                .userId(updated.getUserId())
                .nombre(updated.getNombre())
                .apellido(updated.getApellido())
                .dni(updated.getDni())
                .email(updated.getEmail())
                .telefono(updated.getTelefono())
                .build();
    }

    /**
     * Elimina al usuario por ID (usado en rollback o mantenimiento interno).
     */
    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
        log.info("Usuario eliminado con id: {}", id);
    }
}
