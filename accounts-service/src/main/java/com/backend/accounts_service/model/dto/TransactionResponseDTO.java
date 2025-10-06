package com.backend.accounts_service.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TransactionResponseDTO {
    private String id;
    private String accountId;
    private Double amount;
    private LocalDateTime dated;
    private String description;
    private String origin;
    private String destination;
    private String type;
}
