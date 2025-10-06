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

    // 🔹 Obtener todas las transacciones de una cuenta
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

    // 🔹 Obtener las últimas 5 transacciones
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

    // 🔹 Crear una nueva transacción
    @Operation(
            summary = "Registrar una nueva transacción",
            description = """
                    Registra una nueva transacción asociada a una cuenta virtual (CVU).
                    Puede representar un ingreso (depósito, transferencia recibida) o egreso (pago, envío de dinero).
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
                                              "originCvu": "0001234500006789012345",
                                              "destinationCvu": "0009876500004321098765",
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
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o cuenta no válida"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @RequestBody TransactionRequestDTO request) {
        TransactionResponseDTO response = transactionService.saveTransaction(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
