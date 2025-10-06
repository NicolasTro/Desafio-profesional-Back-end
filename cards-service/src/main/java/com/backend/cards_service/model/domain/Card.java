package com.backend.cards_service.model.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cards")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor

public class Card {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String accountId;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false, unique = true)
    private String number;

    @Column(nullable = false)
    private String provider;

    @Column(nullable = false)
    private String expiration;
}
