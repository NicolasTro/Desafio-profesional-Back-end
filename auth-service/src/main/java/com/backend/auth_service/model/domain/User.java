package com.backend.auth_service.model.domain;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {
    @Id
    private Long id;

    private String email;
    private String password;
}
