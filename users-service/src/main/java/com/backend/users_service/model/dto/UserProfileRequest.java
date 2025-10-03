package com.backend.users_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileRequest {
    private String userId;
    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
}
