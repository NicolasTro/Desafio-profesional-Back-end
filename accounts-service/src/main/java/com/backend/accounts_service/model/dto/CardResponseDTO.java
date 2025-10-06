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

    private String id;            // UUID de la tarjeta
    private String accountId;     // CVU o identificador de cuenta asociada
    private String type;          // Tipo de tarjeta: CREDIT / DEBIT / PREPAID
    private String provider;      // Proveedor: VISA / MASTERCARD / AMEX, etc.
    private String numberMasked;  // Número enmascarado (**** **** **** 1234)
    private String expiration;    // Fecha de vencimiento (MM/YY o MM/YYYY)
}
