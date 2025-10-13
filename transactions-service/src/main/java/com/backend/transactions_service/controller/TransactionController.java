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
        description = "Gestión de transacciones (ingresos, egresos y transferencias) dentro de Digital Money House"
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
            description = """
                    Devuelve todas las transacciones asociadas a una cuenta identificada por su CVU o accountId.
                    Incluye ingresos, egresos y transferencias internas.
                    """,
            parameters = {
                    @Parameter(name = "accountId", description = "Identificador único o CVU de la cuenta", example = "0001234500006789012345")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transacciones obtenidas correctamente"),
                    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
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
            description = """
                    Devuelve las 5 transacciones más recientes asociadas a la cuenta indicada.
                    Ideal para mostrar en el dashboard del usuario.
                    """,
            parameters = {
                    @Parameter(name = "accountId", description = "Identificador único o CVU de la cuenta", example = "0001234500006789012345")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transacciones obtenidas correctamente"),
                    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
            }
    )
    @GetMapping("/{accountId}/last5")
    public ResponseEntity<List<TransactionResponseDTO>> getLast5ByAccount(@PathVariable String accountId) {
        return ResponseEntity.ok(transactionService.findLast5ByAccountId(accountId));
    }

    // =============================================================
    // 🔹 Crear una nueva transacción (uso general)
    // =============================================================
    @Operation(
            summary = "Registrar una nueva transacción general",
            description = """
                    Registra una nueva transacción asociada a una cuenta virtual (CVU).
                    Puede representar un ingreso (depósito, transferencia recibida) o egreso (pago, envío de dinero).
                    Este endpoint es de uso interno y no modifica balances.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la transacción a registrar",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TransactionRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de transacción",
                                    value = """
                                            {
                                              "origin": "0001234500006789012345",
                                              "destination": "0009876500004321098765",
                                              "amount": 1500.00,
                                              "description": "Pago de servicios",
                                              "type": "OUTCOME"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Transacción registrada correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o monto no válido"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(@RequestBody TransactionRequestDTO request) {
        TransactionResponseDTO response = transactionService.saveTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =============================================================
    // 🆕 Registrar ingreso de dinero (Depósito)
    // =============================================================
    @Operation(
            summary = "Registrar ingreso de dinero (depósito)",
            description = """
                    Endpoint utilizado por el `accounts-service` para registrar un ingreso de dinero (depósito) en una cuenta.
                    Este endpoint **solo registra la transacción contable**, ya que el balance se actualiza dentro de `accounts-service`.
                    """,
            parameters = {
                    @Parameter(name = "accountId", description = "Identificador interno o CVU de la cuenta", example = "2424522743941613290685")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del depósito a registrar",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = TransactionRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de ingreso de dinero",
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
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Depósito registrado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o monto incorrecto"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    @PostMapping("/{accountId}/transferences")
    public ResponseEntity<TransactionResponseDTO> registerDeposit(
            @PathVariable String accountId,
            @RequestBody TransactionRequestDTO request) {
        TransactionResponseDTO response = transactionService.registerDeposit(accountId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =============================================================
    // 🔹 Obtener actividad completa
    // =============================================================
    @Operation(
            summary = "Obtener actividad completa de una cuenta",
            description = "Devuelve el historial completo de transacciones de la cuenta indicada.",
            parameters = {
                    @Parameter(name = "accountId", description = "Identificador único de la cuenta", example = "0001234500006789012345")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Actividad obtenida correctamente"),
                    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
            }
    )
    @GetMapping("/{accountId}/activity")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(@PathVariable String accountId) {
        List<TransactionResponseDTO> activity = transactionService.getAllTransactions(accountId);
        return ResponseEntity.ok(activity);
    }

    // =============================================================
    // 🔹 Obtener una transacción específica
    // =============================================================
    @Operation(
            summary = "Obtener una transacción específica",
            description = "Devuelve los detalles de una transacción individual dentro de una cuenta.",
            parameters = {
                    @Parameter(name = "accountId", description = "CVU o identificador de la cuenta", example = "0001234500006789012345"),
                    @Parameter(name = "transferenceId", description = "Identificador de la transacción", example = "uuid-transaccion-123")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transacción encontrada correctamente"),
                    @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
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
