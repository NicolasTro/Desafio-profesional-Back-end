package com.backend.auth_service.model.dto;

import lombok.Data;

@Data
public class AccountResponseDTO {
    private String id;
    private String userId;
    private String cvu;
    private String alias;
    private Double balance;
    private String currency;
}
