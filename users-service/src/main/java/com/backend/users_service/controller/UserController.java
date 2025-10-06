package com.backend.users_service.controller;

import com.backend.users_service.model.dto.UserProfileRequest;
import com.backend.users_service.model.dto.RegisterResponse;
import com.backend.users_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Crea un nuevo usuario.
     * Este endpoint es llamado internamente por auth-service durante el proceso de registro.
     */
    @PostMapping
    public ResponseEntity<RegisterResponse> createUser(@Valid @RequestBody UserProfileRequest request) {
        RegisterResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    /**
     * Actualiza los datos de un usuario existente.
     */
    @PatchMapping("/{id}")
    public ResponseEntity<RegisterResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserProfileRequest request) {

        // Si el body trae userId, validar que coincida con el del path
        if (request.getUserId() != null && !request.getUserId().equals(id)) {
            throw new IllegalArgumentException("El userId del body no coincide con el ID del path");
        }

        RegisterResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RegisterResponse> getUserById(@PathVariable String id) {
        RegisterResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }


    /**
     * Elimina un usuario por ID (usado en rollback o mantenimiento interno).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
