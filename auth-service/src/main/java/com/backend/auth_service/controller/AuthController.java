package com.backend.auth_service.controller;

import com.backend.auth_service.model.dto.LoginRequest;
import com.backend.auth_service.model.dto.LoginResponse;
import com.backend.auth_service.model.dto.RegisterRequest;
import com.backend.auth_service.model.dto.RegisterResponse;
import com.backend.auth_service.service.AuthService;
import com.backend.auth_service.service.RegistrationSagaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(
        name = "Autenticación",
        description = "Endpoints de registro, inicio y cierre de sesión de usuarios en Digital Money House"
)
public class AuthController {

    private final AuthService authService;
    private final RegistrationSagaService sagaService;

    // 🔹 Registro de usuario
    @Operation(
            summary = "Registrar nuevo usuario",
            description = """
                    Orquesta el proceso completo de registro de un nuevo usuario:
                    1. Crea las credenciales en `auth-service`.
                    2. Crea el perfil del usuario en `users-service`.
                    3. Crea la cuenta asociada en `accounts-service`.
                    Devuelve la información consolidada del usuario registrado.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de registro del nuevo usuario",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de registro",
                                    value = """
                                            {
                                              "nombre": "Juan",
                                              "apellido": "Pérez",
                                              "dni": "17268556",
                                              "email": "juan.perez@mail.com",
                                              "telefono": "099123456",
                                              "password": "miClaveSegura123"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Usuario registrado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o usuario ya existente"),
                    @ApiResponse(responseCode = "500", description = "Error interno durante el registro")
            }
    )
    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody RegisterRequest request) {
        RegisterResponse response = sagaService.register(request);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    // 🔹 Inicio de sesión
    @Operation(
            summary = "Iniciar sesión",
            description = """
                    Permite a un usuario autenticarse en el sistema utilizando su correo electrónico y contraseña.
                    Devuelve un token JWT válido para futuras operaciones autenticadas.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Credenciales del usuario",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginRequest.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de login",
                                    value = """
                                            {
                                              "email": "juan.perez@mail.com",
                                              "password": "miClaveSegura123"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, se devuelve token JWT"),
                    @ApiResponse(responseCode = "404", description = "Usuario inexistente"),
                    @ApiResponse(responseCode = "400", description = "Contraseña incorrecta"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    // 🔹 Cierre de sesión (opcional si implementás logout con JWT)
    @Operation(
            summary = "Cerrar sesión",
            description = """
                    Invalida el token JWT actual del usuario.
                    Debe enviarse el token en el encabezado Authorization.
                    """,
            responses = {
                    @ApiResponse(responseCode = "200", description = "Sesión cerrada correctamente"),
                    @ApiResponse(responseCode = "401", description = "Token inválido o ausente"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Implementar invalidación del token si corresponde (lista negra o expiración)
        return ResponseEntity.ok().build();
    }
}
