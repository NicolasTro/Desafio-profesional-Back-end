package com.backend.accounts_service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(name = "TransactionRequestDTO", description = "DTO para solicitudes de transacciones/depósitos")
public class TransactionRequestDTO {
    @Schema(description = "Identificador interno o CVU de la cuenta destino/afectada", example = "2424522743941613290685")
    private String accountId;

    @Schema(description = "Monto de la transacción", example = "2500.00")
    private Double amount;

    @Schema(description = "Descripción opcional de la transacción", example = "Carga con tarjeta VISA 4567")
    private String description;

    @Schema(description = "Origen de la operación (TARJETA, TRANSFER, INTERNAL)", example = "TARJETA")
    private String origin;

    @Schema(description = "Destino de la operación (CVU o accountId)", example = "2424522743941613290685")
    private String destination;

    @Schema(description = "Identificador opcional de la tarjeta asociada (obligatorio si origin == 'TARJETA')", example = "card-abc-123")

    private String cardId;

    @Schema(description = "Tipo de transacción (DEPOSIT, OUTCOME, TRANSFER)", example = "DEPOSIT")
    private String type;
}
