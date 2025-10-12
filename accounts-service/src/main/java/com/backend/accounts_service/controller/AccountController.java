package com.backend.accounts_service.controller;

import com.backend.accounts_service.model.dto.*;
import com.backend.accounts_service.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/accounts")
@Tag(
        name = "Cuentas",
        description = "Gestión de cuentas virtuales, tarjetas y transacciones dentro de Digital Money House"
)
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    // =========================================================
    // 🔹 Crear cuenta
    // =========================================================
    @Operation(
            summary = "Crear una nueva cuenta",
            description = """
                    Crea una cuenta asociada a un usuario específico durante el registro.
                    Este endpoint es utilizado internamente por el `auth-service` como parte del proceso de registro.
                    """,
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos necesarios para crear una cuenta",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = AccountCreateDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de creación de cuenta",
                                    value = """
                                            {
                                              "userId": "uuid-usuario-123",
                                              "alias": "juan.perez.dmh",
                                              "currency": "ARS"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Cuenta creada correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody AccountCreateDTO dto) {
        log.info("📥 Creando nueva cuenta...");
        AccountResponseDTO response = accountService.createAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================================================
    // 🔹 Nuevo endpoint: Ingreso de dinero (Depósito)
    // =========================================================
    @Operation(
            summary = "Registrar ingreso de dinero en la cuenta",
            description = """
                    Registra un ingreso de dinero (depósito) desde una tarjeta de crédito o débito hacia la cuenta del usuario.
                    Este endpoint coordina con el `transactions-service` utilizando resiliencia (Retry + CircuitBreaker).
                    Si ocurre un fallo en el registro de la transacción, se revierte automáticamente el saldo.
                    """,
            parameters = {
                    @Parameter(name = "accountId", description = "Identificador interno de la cuenta", example = "uuid-cuenta-123")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos del ingreso de dinero",
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
                    @ApiResponse(responseCode = "201", description = "Depósito realizado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o saldo incorrecto"),
                    @ApiResponse(responseCode = "500", description = "Error al registrar la transacción o servicio no disponible")
            }
    )
    @PostMapping("/{accountId}/transferences")
    public ResponseEntity<TransactionResponseDTO> registerDeposit(
        @PathVariable("accountId") String accountId,
            @RequestBody TransactionRequestDTO request) {

        log.info("💰 Iniciando depósito para la cuenta {}", accountId);
        TransactionResponseDTO response = accountService.registerDeposit(accountId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // =========================================================
    // 🔹 Actualizar cuenta
    // =========================================================
    @Operation(summary = "Actualizar datos de una cuenta", description = "Permite modificar alias o moneda.")
    @PatchMapping("/{cvu}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @PathVariable String cvu,
            @RequestBody AccountUpdateDTO request) {
        return ResponseEntity.ok(accountService.updateAccount(cvu, request));
    }

    // =========================================================
    // 🔹 Eliminar cuenta
    // =========================================================
    @Operation(summary = "Eliminar cuenta", description = "Elimina una cuenta por su ID interno.")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // 🔹 Obtener cuenta por CVU
    // =========================================================
    @Operation(summary = "Obtener cuenta por CVU")
    @GetMapping("/{cvu}")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable String cvu) {
        return ResponseEntity.ok(accountService.getAccountByCvu(cvu));
    }

    // =========================================================
    // 🔹 Obtener cuenta por userId
    // =========================================================
    @Operation(summary = "Obtener cuenta por userId")
    @GetMapping("/user/{userId}")
    public ResponseEntity<AccountResponseDTO> getAccountsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(accountService.getAccountByUserId(userId));
    }

    // =========================================================
    // 🔹 Tarjetas (cards-service)
    // =========================================================
    @GetMapping("/{cvu}/cards")
        public ResponseEntity<List<CardResponseDTO>> getCards(@PathVariable String cvu) {
                return ResponseEntity.ok(accountService.getCards(cvu));
        }

    @PostMapping("/{cvu}/cards")
    @Operation(summary = "Agregar tarjeta a una cuenta", description = "Registra una nueva tarjeta (débito/crédito) asociada a la cuenta indicada.",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la tarjeta a registrar",
                    required = true,
                    content = @Content(schema = @Schema(implementation = CardRequestDTO.class))
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tarjeta registrada correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "409", description = "Tarjeta ya asociada a otra cuenta")
            }
    )
    public ResponseEntity<CardResponseDTO> addCard(@PathVariable String cvu, @RequestBody CardRequestDTO request) {
        CardResponseDTO response = accountService.addCard(cvu, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{cvu}/cards/{cardId}")
    @Operation(summary = "Obtener tarjeta por ID", description = "Devuelve los datos de una tarjeta asociada a la cuenta indicada.",
            parameters = {@Parameter(name = "cvu", description = "CVU de la cuenta"), @Parameter(name = "cardId", description = "Identificador de la tarjeta")},
            responses = {@ApiResponse(responseCode = "200", description = "Tarjeta encontrada"), @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")}
    )
    public ResponseEntity<CardResponseDTO> getCardById(@PathVariable String cardId, @PathVariable String cvu) {
        return ResponseEntity.ok(accountService.getCardById(cardId, cvu));
    }

    @DeleteMapping("/{cvu}/cards/{cardId}")
    @Operation(summary = "Eliminar tarjeta", description = "Elimina la tarjeta especificada asociada a la cuenta.",
            parameters = {@Parameter(name = "cvu", description = "CVU de la cuenta"), @Parameter(name = "cardId", description = "Identificador de la tarjeta")},
            responses = {@ApiResponse(responseCode = "204", description = "Tarjeta eliminada"), @ApiResponse(responseCode = "404", description = "Cuenta o tarjeta no encontrada")}
    )
    public ResponseEntity<Void> deleteCard(@PathVariable String cvu, @PathVariable String cardId) {
        accountService.deleteCard(cvu, cardId);
        return ResponseEntity.noContent().build();
    }

    // =========================================================
    // 🔹 Actualizar balance (interno)
    // =========================================================
    @PatchMapping("/{cvu}/balance")
    @Operation(summary = "Actualizar balance (interno)", description = "Actualiza el balance de una cuenta. Usado internamente por transactions-service.",
        parameters = {@Parameter(name = "cvu", description = "CVU de la cuenta"),
            @Parameter(name = "amount", description = "Monto a aplicar (positivo)"),
            @Parameter(name = "type", description = "Tipo: CREDIT o DEBIT")},
        responses = {@ApiResponse(responseCode = "200", description = "Balance actualizado"), @ApiResponse(responseCode = "400", description = "Tipo inválido o saldo insuficiente")}
    )
    public ResponseEntity<Void> updateBalance(
        @PathVariable String cvu,
        @RequestParam Double amount,
        @RequestParam String type) {
    accountService.updateBalance(cvu, amount, type);
    return ResponseEntity.ok().build();
    }

    // =========================================================
    // 🔹 Transacciones (transactions-service)
    // =========================================================
    @GetMapping("/{accountId}/activity")
    @Operation(summary = "Obtener transacciones de una cuenta", description = "Devuelve todas las transacciones asociadas a la cuenta indicada.",
            parameters = {@Parameter(name = "accountId", description = "Identificador/CVU de la cuenta")},
            responses = {@ApiResponse(responseCode = "200", description = "Lista de transacciones"), @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")}
    )
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(@PathVariable String accountId) {
        return ResponseEntity.ok(accountService.getAccountTransactions(accountId));
    }

    @GetMapping("/{accountId}/transactions/last5")
    @Operation(summary = "Últimas 5 transacciones", description = "Devuelve las últimas 5 transacciones de la cuenta indicada.",
            parameters = {@Parameter(name = "accountId", description = "Identificador/CVU de la cuenta")},
            responses = {@ApiResponse(responseCode = "200", description = "Lista de hasta 5 transacciones")}
    )
    public ResponseEntity<List<TransactionResponseDTO>> getLast5Transactions(@PathVariable String accountId) {
        return ResponseEntity.ok(accountService.getLast5Transactions(accountId));
    }

    @GetMapping("/{accountId}/activity/{transferenceId}")
    @Operation(summary = "Obtener transacción por ID", description = "Devuelve una transacción específica asociada a la cuenta.",
            parameters = {@Parameter(name = "accountId", description = "Identificador/CVU de la cuenta"), @Parameter(name = "transferenceId", description = "ID de la transacción")},
            responses = {@ApiResponse(responseCode = "200", description = "Transacción encontrada"), @ApiResponse(responseCode = "404", description = "Transacción no encontrada")}
    )
    public ResponseEntity<TransactionResponseDTO> getTransactionByIdAndAccountId(
            @PathVariable("accountId") String accountId,
            @PathVariable("transferenceId") String transferenceId) {
        return ResponseEntity.ok(accountService.getTransactionByIdAndAccountId(accountId, transferenceId));
    }
}
