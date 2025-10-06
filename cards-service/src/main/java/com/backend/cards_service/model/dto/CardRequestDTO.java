package com.backend.cards_service.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Data
public class CardRequestDTO {

    @NotBlank(message = "El ID de cuenta (CVU) es obligatorio")
    private String accountId;

    @NotBlank(message = "El tipo de tarjeta es obligatorio")
    @Pattern(regexp = "^(CREDIT|DEBIT)$",
            message = "El tipo de tarjeta debe ser CREDIT o DEBIT")
    private String type;

    @NotBlank(message = "El número de tarjeta es obligatorio")
    @Pattern(regexp = "^[0-9]{16}$",
            message = "El número de tarjeta debe tener 16 dígitos numéricos")
    private String cardNumber; // número completo, usado para calcular provider y masked

    @NotBlank(message = "La fecha de expiración es obligatoria")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$",
            message = "La expiración debe tener formato MM/YY con mes válido")
    private String expiration;

    // Validar que la expiración sea futura
    @jakarta.validation.constraints.AssertTrue(message = "La fecha de expiración es inválida o ya venció")
    private boolean isExpiryInFuture() {
        try {
            YearMonth exp = YearMonth.parse(expiration, DateTimeFormatter.ofPattern("MM/yy"));
            return exp.isAfter(YearMonth.now());
        } catch (Exception e) {
            return false;
        }
    }
}
