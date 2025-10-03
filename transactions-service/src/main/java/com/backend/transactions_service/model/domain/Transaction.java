package com.backend.transactions_service.model.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(nullable = false)
    private String accountId; // CVU de la cuenta asociada a esta transacción

    @Column(nullable = false)
    private Double amount;

    @Column(nullable = false)
    private LocalDateTime dated;

    private String description;

    @Column(nullable = false)
    private String origin; // CVU origen

    @Column(nullable = false)
    private String destination; // CVU destino

    @Enumerated(EnumType.STRING)
    private TransactionType type; // CREDIT | DEBIT | TRANSFER
}
