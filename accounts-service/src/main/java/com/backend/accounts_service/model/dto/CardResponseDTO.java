package com.backend.accounts_service.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de respuesta que representa una tarjeta registrada en el sistema.
 * Se usa tanto en accounts-service (Feign) como en cards-service.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CardResponseDTO {

    private String id;
    private String accountId;
    private String type;
    private String provider;
    private String numberMasked;
    private String expiration;
}
