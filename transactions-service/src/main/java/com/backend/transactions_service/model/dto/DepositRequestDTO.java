package com.backend.transactions_service.model.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class DepositRequestDTO {
    private String accountId;
    private String cardId;
    private Double amount;
}
