package com.backend.transactions_service.model.dto;

import com.backend.transactions_service.model.domain.TransactionType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Schema(name = "TransactionResponseDTO", description = "Representación de una transacción registrada")
public class TransactionResponseDTO {
    @Schema(description = "Identificador de la transacción", example = "uuid-transaccion-123")
    private String id;

    @Schema(description = "CVU o accountId asociado a la transacción", example = "2424522743941613290685")
    private String accountId;

    @Schema(description = "Monto de la transacción", example = "2500.00")
    private Double amount;

    @Schema(description = "Fecha y hora de la transacción")
    private LocalDateTime dated;

    @Schema(description = "Descripción de la operación", example = "Carga con tarjeta VISA 4567")
    private String description;

    @Schema(description = "Origen de la operación", example = "TARJETA")
    private String origin;

    @Schema(description = "Destino de la operación", example = "2424522743941613290685")
    private String destination;

    @Schema(description = "Identificador de la tarjeta asociada (si aplica)", example = "card-abc-123")
    private String cardId;

    @Schema(description = "Tipo de la transacción", example = "DEPOSIT")
    private TransactionType type;
}
