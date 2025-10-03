package com.backend.auth_service.model.dto;

import lombok.Data;

@Data
public class AccountResponseDTO {
    private String id;       // id interno de la cuenta (UUID)
    private String userId;   // referencia al usuario dueño de la cuenta
    private String cvu;      // número único de 22 dígitos
    private String alias;    // alias único de la cuenta
    private Double balance;  // saldo inicial (0.0)
    private String currency; // moneda, por defecto "ARS"
}
