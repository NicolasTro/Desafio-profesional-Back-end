package com.backend.users_service.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    private String userId; // mismo UUID generado en auth-service

    private String nombre;
    private String apellido;
    private String dni;
    private String email;
    private String telefono;
}
