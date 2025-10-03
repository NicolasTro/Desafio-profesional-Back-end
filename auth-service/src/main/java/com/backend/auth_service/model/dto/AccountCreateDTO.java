package com.backend.auth_service.model.dto;

import lombok.Data;

@Data
public class AccountCreateDTO {
    private String userId; // el ID del usuario al que se le crea la cuenta
}
