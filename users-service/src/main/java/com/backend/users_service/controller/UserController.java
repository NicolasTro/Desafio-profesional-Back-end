package com.backend.users_service.controller;

import com.backend.users_service.model.dto.UserProfileRequest;
import com.backend.users_service.model.dto.RegisterResponse;
import com.backend.users_service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador de gestión de usuarios del sistema Digital Money House.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "Gestión de usuarios y perfiles dentro de Digital Money House")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 🔹 Crear usuario
    @Operation(
            summary = "Registrar nuevo usuario",
            description = """
                    Crea un nuevo usuario dentro del sistema Digital Money House.
                    Este endpoint es llamado internamente por `auth-service` durante el proceso de registro de usuario.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario creado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o incompletos"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    @PostMapping
    public ResponseEntity<RegisterResponse> createUser(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del perfil del usuario a registrar",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UserProfileRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de usuario",
                                    value = """
                                            {
                                              "nombre": "Juan",
                                              "apellido": "Pérez",
                                              "dni": "17268556",
                                              "email": "juan.perez@mail.com",
                                              "telefono": "099123456"
                                            }
                                            """
                            )
                    )
            )
            @Valid @RequestBody UserProfileRequest request) {

        RegisterResponse response = userService.register(request);
        return ResponseEntity.ok(response);
    }

    // 🔹 Actualizar usuario
    @Operation(
            summary = "Actualizar datos de usuario",
            description = "Actualiza los datos personales o de contacto de un usuario existente.",
            parameters = {
                    @Parameter(name = "id", description = "Identificador único del usuario", example = "b1a8c5d3-4f17-45c8-9c82-d61e8c6d23b2")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
                    @ApiResponse(responseCode = "400", description = "El ID del path no coincide con el del cuerpo o los datos son inválidos"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            }
    )
    @PatchMapping("/{id}")
    public ResponseEntity<RegisterResponse> updateUser(
            @PathVariable String id,
            @Valid @RequestBody UserProfileRequest request) {

        if (request.getUserId() != null && !request.getUserId().equals(id)) {
            throw new IllegalArgumentException("El userId del body no coincide con el ID del path");
        }

        RegisterResponse response = userService.updateUser(id, request);
        return ResponseEntity.ok(response);
    }

    // 🔹 Obtener usuario por ID
    @Operation(
            summary = "Obtener usuario por ID",
            description = "Devuelve la información completa del usuario asociado al ID proporcionado.",
            parameters = {
                    @Parameter(name = "id", description = "Identificador único del usuario", example = "b1a8c5d3-4f17-45c8-9c82-d61e8c6d23b2")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            }
    )
    @GetMapping("/{id}")
    public ResponseEntity<RegisterResponse> getUserById(@PathVariable String id) {
        RegisterResponse response = userService.getUserById(id);
        return ResponseEntity.ok(response);
    }

    // 🔹 Eliminar usuario
    @Operation(
            summary = "Eliminar usuario por ID",
            description = """
                    Elimina permanentemente un usuario del sistema.
                    Este endpoint se utiliza en procesos internos o rollbacks automáticos.
                    """,
            parameters = {
                    @Parameter(name = "id", description = "Identificador único del usuario", example = "b1a8c5d3-4f17-45c8-9c82-d61e8c6d23b2")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente (sin contenido)"),
                    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}
