package com.backend.cards_service.model.dto;

import lombok.Data;

@Data
public class CardResponseDTO {
    private String id;
    private String accountId;
    private String type;
    private String numberMasked;
    private String provider;
    private String expiration;
}
