package com.backend.auth_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta estándar para el flujo de registro de usuario.
 * Se usa tanto para la respuesta final del registro como para
 * la comunicación entre microservicios (users, accounts).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterResponse {

    private String userId;
    private String nombre;
    private String apellido;
    private String email;
    private String telefono;

    // Se completan en account-service
    private String cvu;
    private String alias;
}
