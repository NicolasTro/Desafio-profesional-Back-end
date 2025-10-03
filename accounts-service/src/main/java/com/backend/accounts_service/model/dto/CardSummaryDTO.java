package com.backend.accounts_service.model.dto;

import lombok.Data;

@Data
public class CardSummaryDTO {
    private String id;
    private String accountId;     // CVU de la cuenta asociada
    private String type;          // CREDIT | DEBIT
    private String provider;      // VISA | MASTERCARD
    private String numberMasked;  // **** **** **** 1234
    private String expiration;    // MM/YY
}
