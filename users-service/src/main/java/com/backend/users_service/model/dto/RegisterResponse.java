package com.backend.users_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representa la respuesta estándar del proceso de registro de usuario.
 * Este DTO es compatible con auth-service y account-service.
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
    private String dni;
    // Estos campos se completan más adelante por account-service
    private String cvu;
    private String alias;
}
