package com.backend.auth_service.model.dto;

import lombok.Data;

@Data
public class RegisterResponse {
    private String id;
    private String email;
    private String nombre;
    private String apellido;
    private String dni;
    private String telefono;
    private String cvu;
    private String alias;
}
