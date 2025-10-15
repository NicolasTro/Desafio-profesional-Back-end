package com.backend.transactions_service.controller;

import com.backend.transactions_service.model.dto.TransactionRequestDTO;
import com.backend.transactions_service.model.dto.TransactionResponseDTO;
import com.backend.transactions_service.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@Tag(
        name = "Transacciones",
        description = "Gestión de transacciones (depósitos, egresos y transferencias) dentro de Digital Money House"
)
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    // =============================================================
    // 🔹 Obtener todas las transacciones de una cuenta
    // =============================================================
    @Operation(
            summary = "Obtener todas las transacciones de una cuenta",
            description = "Devuelve todas las transacciones asociadas a una cuenta identificada por su CVU o accountId.",
            parameters = @Parameter(name = "accountId", example = "0001234500006789012345")
    )
    @GetMapping("/{accountId}")
    public ResponseEntity<List<TransactionResponseDTO>> getAllByAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(transactionService.findAllByAccountId(accountId));
    }

    // =============================================================
    // 🔹 Obtener las últimas 5 transacciones
    // =============================================================
    @Operation(
            summary = "Obtener las últimas 5 transacciones",
            description = "Devuelve las 5 transacciones más recientes asociadas a la cuenta indicada.",
            parameters = @Parameter(name = "accountId", example = "0001234500006789012345")
    )
    @GetMapping("/{accountId}/last5")
    public ResponseEntity<List<TransactionResponseDTO>> getLast5ByAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(transactionService.findLast5ByAccountId(accountId));
    }

    // =============================================================
    // 💳 Registrar ingreso de dinero (Depósito)
    // =============================================================
    @Operation(
            summary = "Registrar ingreso de dinero (depósito)",
            description = """
                    Endpoint utilizado por el `accounts-service` para registrar un depósito.
                    Este endpoint **solo registra la transacción contable** (no actualiza balances).
                    """,
            parameters = @Parameter(name = "accountId", example = "2424522743941613290685"),
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del depósito a registrar",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TransactionRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de depósito",
                                    value = """
                                            {
                                              "amount": 2500.00,
                                              "description": "Carga con tarjeta VISA 4567",
                                              "origin": "TARJETA",
                                              "destination": "2424522743941613290685",
                                              "cardId": "card-abc-123",
                                              "type": "DEPOSIT"
                                            }
                                            """
                            )
                    )
            )
    )
    @PostMapping("/{accountId}/transferences")
    public ResponseEntity<TransactionResponseDTO> registerDeposit(
            @PathVariable String accountId,
            @RequestBody TransactionRequestDTO request) {

        TransactionResponseDTO response = transactionService.registerDeposit(accountId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =============================================================
    // 🔁 Registrar transferencia entre cuentas
    // =============================================================
    @Operation(
            summary = "Registrar transferencia entre cuentas",
            description = """
                    Endpoint utilizado por el `accounts-service` durante una transferencia.
                    Registra ambas operaciones contables:
                    - **DEBIT:** en la cuenta origen.
                    - **CREDIT:** en la cuenta destino.
                    
                    Este endpoint no actualiza balances; solo registra el movimiento contable.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la transferencia a registrar",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TransactionRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de transferencia",
                                    value = """
                                            {
                                              "amount": 500.00,
                                              "description": "Transferencia a otra cuenta",
                                              "accountId": "0001112223334445556666",
                                              "destination": "9998887776665554443333",
                                              "type": "TRANSFER"
                                            }
                                            """
                            )
                    )
            )
    )
    @PostMapping("/transfer")
    public ResponseEntity<TransactionResponseDTO> registerTransfer(@RequestBody TransactionRequestDTO request) {
        TransactionResponseDTO response = transactionService.transfer(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =============================================================
    // 🔹 Obtener actividad completa
    // =============================================================
    @Operation(
            summary = "Obtener actividad completa de una cuenta",
            description = "Devuelve el historial completo de transacciones de la cuenta indicada.",
            parameters = @Parameter(name = "accountId", example = "0001234500006789012345")
    )
    @GetMapping("/{accountId}/activity")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(@PathVariable String accountId) {
        return ResponseEntity.ok(transactionService.getAllTransactions(accountId));
    }

    // =============================================================
    // 🔹 Obtener una transacción específica
    // =============================================================
    @Operation(
            summary = "Obtener una transacción específica",
            description = "Devuelve los detalles de una transacción individual dentro de una cuenta.",
            parameters = {
                    @Parameter(name = "accountId", example = "0001234500006789012345"),
                    @Parameter(name = "transferenceId", example = "uuid-transaccion-123")
            }
    )
    @GetMapping("/{accountId}/activity/{transferenceId}")
    public ResponseEntity<TransactionResponseDTO> getTransactionById(
            @PathVariable String accountId,
            @PathVariable String transferenceId) {

        TransactionResponseDTO transaction =
                transactionService.getTransactionByIdAndAccountId(accountId, transferenceId);
        return ResponseEntity.ok(transaction);
    }
}
