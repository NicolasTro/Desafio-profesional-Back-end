package com.backend.auth_service.client;

import com.backend.auth_service.config.FeignConfig;
import com.backend.auth_service.model.dto.UserProfileRequest;
import com.backend.auth_service.model.dto.RegisterResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * Cliente Feign para comunicarse con users-service.
 * Se utiliza durante el proceso de registro orquestado por auth-service.
 */
@FeignClient(name = "users-service", configuration = FeignConfig.class)
public interface UsersClient {

    /**
     * Crea un nuevo usuario en users-service.
     * Este método es invocado por auth-service durante la saga de registro.
     */
    @PostMapping("/users")
    RegisterResponse createUser(@RequestBody UserProfileRequest request);

    /**
     * Actualiza un usuario existente.
     */
    @PatchMapping("/users/{id}")
    RegisterResponse updateUser(@PathVariable String id, @RequestBody UserProfileRequest request);

    /**
     * Elimina un usuario (usado para rollback si la saga falla).
     */
    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable String id);
}
