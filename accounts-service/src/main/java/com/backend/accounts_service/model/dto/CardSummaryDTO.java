package com.backend.accounts_service.model.dto;

import lombok.Data;

@Data
public class CardSummaryDTO {
    private String id;
    private String accountId;
    private String type;
    private String provider;
    private String numberMasked;
    private String expiration;
}
