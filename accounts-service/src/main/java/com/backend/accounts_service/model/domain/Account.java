package com.backend.accounts_service.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "accounts")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false, unique = true)
    private String userId; // referencia al usuario dueño de la cuenta

    @Column(nullable = false, unique = true, length = 22)
    private String cvu; // ya generado en users-service

    @Column(nullable = false, unique = true)
    private String alias; // ya generado en users-service

    @Column(nullable = false)
    private Double balance = 0.0; // saldo inicial

    @Column(nullable = false)
    private String currency = "ARS"; // moneda por defecto
}
