package com.backend.transactions_service.model.dto;

import com.backend.transactions_service.model.domain.TransactionType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class TransactionRequestDTO {
    private String accountId;   // CVU de la cuenta que registra la transacción
    private Double amount;
    private LocalDateTime dated;
    private String description;
    private String origin;      // CVU origen
    private String destination; // CVU destino
    private TransactionType type;        // CREDIT | DEBIT | TRANSFER
}
