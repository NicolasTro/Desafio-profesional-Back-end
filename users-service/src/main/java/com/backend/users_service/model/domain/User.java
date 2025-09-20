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
    private String id;  // mismo ID que en Auth

    private String nombre;
    private String apellido;

    @Column(unique = true, nullable = false)
    private String dni;

    @Column(unique = true, nullable = false)
    private String email;

    private String telefono;

    @Column(unique = true, length = 22)
    private String cvu;

    @Column(unique = true)
    private String alias;
}

