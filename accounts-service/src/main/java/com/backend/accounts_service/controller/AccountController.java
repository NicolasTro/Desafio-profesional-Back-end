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

    // 🔹 Crear cuenta
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

    // 🔹 Actualizar cuenta
    @Operation(
            summary = "Actualizar datos de una cuenta",
            description = "Permite modificar los datos de una cuenta existente, como alias o moneda.",
            parameters = {
                    @Parameter(name = "cvu", description = "CVU de la cuenta", example = "0001234500006789012345")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cuenta actualizada correctamente"),
                    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
            }
    )
    @PatchMapping("/{cvu}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @PathVariable String cvu,
            @RequestBody AccountUpdateDTO request) {
        return ResponseEntity.ok(accountService.updateAccount(cvu, request));
    }

    // 🔹 Eliminar cuenta
    @Operation(
            summary = "Eliminar cuenta",
            description = "Elimina una cuenta por su identificador. Usado en rollbacks o mantenimiento interno.",
            parameters = {
                    @Parameter(name = "id", description = "ID interno de la cuenta", example = "uuid-cuenta-123")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Cuenta eliminada correctamente"),
                    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
            }
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Obtener cuenta por CVU
    @Operation(
            summary = "Obtener cuenta por CVU",
            description = "Devuelve la información completa de una cuenta según su CVU.",
            parameters = {
                    @Parameter(name = "cvu", description = "CVU de la cuenta", example = "0001234500006789012345")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cuenta encontrada correctamente"),
                    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
            }
    )
    @GetMapping("/{cvu}")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable String cvu) {
        return ResponseEntity.ok(accountService.getAccountByCvu(cvu));
    }

    // 🔹 Obtener cuentas por usuario
    @Operation(
            summary = "Obtener cuenta por userId",
            description = "Devuelve la cuenta asociada a un usuario específico.",
            parameters = {
                    @Parameter(name = "userId", description = "Identificador único del usuario", example = "uuid-usuario-123")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Cuenta encontrada correctamente"),
                    @ApiResponse(responseCode = "404", description = "Usuario o cuenta no encontrada")
            }
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<AccountResponseDTO> getAccountsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(accountService.getAccountByUserId(userId));
    }

    // 🔹 Obtener tarjetas
    @Operation(
            summary = "Obtener todas las tarjetas asociadas a un CVU",
            description = "Devuelve todas las tarjetas asociadas a una cuenta.",
            parameters = {
                    @Parameter(name = "cvu", description = "CVU de la cuenta", example = "0001234500006789012345")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarjetas obtenidas correctamente"),
                    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
            }
    )
    @GetMapping("/{cvu}/cards")
    public ResponseEntity<List<CardSummaryDTO>> getCards(@PathVariable String cvu) {
        return ResponseEntity.ok(accountService.getCards(cvu));
    }

    // 🔹 Agregar tarjeta
    @Operation(
            summary = "Agregar una nueva tarjeta a una cuenta",
            description = "Asocia una nueva tarjeta (crédito o débito) a una cuenta existente.",
            parameters = {
                    @Parameter(name = "cvu", description = "CVU de la cuenta", example = "0001234500006789012345")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la tarjeta a asociar",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = CardRequestDTO.class),
                            examples = @ExampleObject(
                                    name = "Ejemplo de tarjeta",
                                    value = """
                                            {
                                              "cardNumber": "4111111111111111",
                                              "cardHolder": "Juan Pérez",
                                              "expiryDate": "12/28",
                                              "cvv": "123",
                                              "type": "CREDIT"
                                            }
                                            """
                            )
                    )
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Tarjeta asociada correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                    @ApiResponse(responseCode = "409", description = "La tarjeta ya está asociada a otra cuenta")
            }
    )
    @PostMapping("/{cvu}/cards")
    public ResponseEntity<CardResponseDTO> addCard(
            @PathVariable String cvu,
            @RequestBody CardRequestDTO request) {
        CardResponseDTO response = accountService.addCard(cvu, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 🔹 Obtener tarjeta específica
    @Operation(
            summary = "Obtener una tarjeta específica",
            description = "Devuelve los datos de una tarjeta determinada asociada a la cuenta indicada.",
            parameters = {
                    @Parameter(name = "cvu", description = "CVU de la cuenta", example = "0001234500006789012345"),
                    @Parameter(name = "cardId", description = "Identificador único de la tarjeta", example = "uuid-tarjeta-123")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarjeta encontrada correctamente"),
                    @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
            }
    )
    @GetMapping("/{cvu}/cards/{cardId}")
    public ResponseEntity<CardResponseDTO> getCardById(
            @PathVariable String cvu,
            @PathVariable String cardId) {
        return ResponseEntity.ok(accountService.getCardById(cvu, cardId));
    }

    // 🔹 Eliminar tarjeta
    @Operation(
            summary = "Eliminar una tarjeta asociada",
            description = "Elimina una tarjeta específica asociada a la cuenta indicada.",
            parameters = {
                    @Parameter(name = "cvu", description = "CVU de la cuenta", example = "0001234500006789012345"),
                    @Parameter(name = "cardId", description = "Identificador único de la tarjeta", example = "uuid-tarjeta-123")
            },
            responses = {
                    @ApiResponse(responseCode = "204", description = "Tarjeta eliminada correctamente"),
                    @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
            }
    )
    @DeleteMapping("/{cvu}/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable String cvu,
            @PathVariable String cardId) {
        accountService.deleteCard(cvu, cardId);
        return ResponseEntity.noContent().build();
    }

    // 🔹 Actualizar balance
    @Operation(
            summary = "Actualizar balance de la cuenta",
            description = """
                    Actualiza el saldo de una cuenta según una transacción (ingreso o egreso).
                    Usado internamente por `transactions-service`.
                    """,
            parameters = {
                    @Parameter(name = "cvu", description = "CVU de la cuenta", example = "0001234500006789012345"),
                    @Parameter(name = "amount", description = "Monto a modificar", example = "2500.00"),
                    @Parameter(name = "type", description = "Tipo de operación (INCOME o OUTCOME)", example = "INCOME")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Balance actualizado correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o tipo de operación incorrecto")
            }
    )
    @PatchMapping("/{cvu}/balance")
    public ResponseEntity<Void> updateBalance(
            @PathVariable String cvu,
            @RequestParam Double amount,
            @RequestParam String type) {
        accountService.updateBalance(cvu, amount, type);
        return ResponseEntity.ok().build();
    }

    // 🔹 Obtener todas las transacciones
    @Operation(
            summary = "Obtener todas las transacciones de una cuenta",
            description = "Devuelve todas las transacciones asociadas a una cuenta determinada.",
            parameters = {
                    @Parameter(name = "cvu", description = "CVU de la cuenta", example = "0001234500006789012345")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transacciones obtenidas correctamente"),
                    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
            }
    )
    @GetMapping("/{accountId}/activity")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(@PathVariable String accountId) {
        return ResponseEntity.ok(accountService.getAccountTransactions(accountId));
    }

    // 🔹 Obtener últimas 5 transacciones
    @Operation(
            summary = "Obtener las últimas 5 transacciones",
            description = "Devuelve las últimas cinco transacciones registradas para la cuenta indicada.",
            parameters = {
                    @Parameter(name = "cvu", description = "CVU de la cuenta", example = "0001234500006789012345")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Transacciones obtenidas correctamente")
            }
    )
    @GetMapping("/{accountId}/transactions/last5")
    public ResponseEntity<List<TransactionResponseDTO>> getLast5Transactions(@PathVariable String accountId) {
        return ResponseEntity.ok(accountService.getLast5Transactions(accountId));
    }




}
