package com.backend.auth_service.model.dto;

import lombok.Data;

@Data
public class RegisterRequest {
    private String email;
    private String password;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String cvu;
    private String alias;
}
