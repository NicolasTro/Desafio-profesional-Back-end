package com.backend.cards_service.model.dto;

import lombok.Data;

@Data
public class CardRequestDTO {
    private String accountId;
    private String type; // CREDIT o DEBIT
    private String numberMasked; // **** **** **** 1234
    private String provider; // VISA, Mastercard, etc.
    private String expiration; // MM/YY
}
