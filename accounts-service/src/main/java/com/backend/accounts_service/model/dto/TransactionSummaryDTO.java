package com.backend.accounts_service.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionSummaryDTO {
    private String id;
    private String accountId;   // CVU de la cuenta
    private Double amount;
    private LocalDateTime dated;
    private String description;
    private String origin;      // CVU origen
    private String destination; // CVU destino
    private String type;        // CREDIT | DEBIT | TRANSFER
}
