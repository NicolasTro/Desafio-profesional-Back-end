package com.backend.auth_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterResponse {
    private String userId;
    private String email;
    private String nombre;
    private String apellido;
    private String cvu;
    private String alias;
}
