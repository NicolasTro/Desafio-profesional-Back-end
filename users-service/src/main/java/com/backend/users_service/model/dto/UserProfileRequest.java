package com.backend.users_service.model.dto;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String userId;   // viene de Auth (id de la tabla users en auth-service)
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
}
