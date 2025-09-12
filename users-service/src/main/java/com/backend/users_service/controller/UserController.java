package com.backend.users_service.controller;

import com.backend.users_service.model.dto.UserRegisterRequest;
import com.backend.users_service.model.domain.User;
import com.backend.users_service.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "Operaciones relacionadas con usuarios")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario con CVU y alias generados automáticamente")
    public ResponseEntity<User> registerUser(@Valid @RequestBody UserRegisterRequest request) {
        User user = userService.registerUser(request);
        return ResponseEntity.ok(user);
    }
}

