package com.backend.accounts_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la creación de una nueva tarjeta.
 * Se usa en la comunicación desde accounts-service hacia cards-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardRequestDTO {

    private String accountId;
    private String type;
    private String provider;
    private String cardNumber;
    private String expiration;
}
