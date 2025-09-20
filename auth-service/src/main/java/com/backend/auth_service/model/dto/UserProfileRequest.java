package com.backend.auth_service.model.dto;

import lombok.Data;

@Data
public class UserProfileRequest {
    private String userId;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
    private String cvu;
    private String alias;
}
