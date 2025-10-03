package com.backend.accounts_service.model.dto;

import lombok.Data;

@Data
public class AccountUpdateDTO {
    private String alias;
    private String currency;
}
