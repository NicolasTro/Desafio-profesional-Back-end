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

    private String accountId;     // CVU o identificador de la cuenta asociada
    private String type;          // Tipo de tarjeta: CREDIT / DEBIT / PREPAID
    private String provider;      // Proveedor: VISA / MASTERCARD / AMEX, etc.
    private String cardNumber;  // Número enmascarado (**** **** **** 1234)
    private String expiration;    // Fecha de vencimiento (MM/YY o MM/YYYY)
}
