package com.backend.accounts_service.model.dto;

import lombok.Data;

@Data
public class AccountResponseDTO {
    private String id;       // UUID interno de la cuenta
    private String userId;   // referencia al usuario dueño
    private String cvu;      // número único de 22 dígitos
    private String alias;    // alias único
    private Double balance;  // saldo
    private String currency; // moneda
}
