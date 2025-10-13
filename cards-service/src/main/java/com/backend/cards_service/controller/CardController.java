package com.backend.cards_service.controller;

import com.backend.cards_service.model.dto.CardRequestDTO;
import com.backend.cards_service.model.dto.CardResponseDTO;
import com.backend.cards_service.service.CardService;
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
@RequestMapping("/cards")
@Tag(
        name = "Tarjetas",
        description = "Gestión de tarjetas de crédito y débito asociadas a una cuenta (CVU) en Digital Money House"
)
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    // 🔹 Obtener todas las tarjetas de un CVU
    @Operation(
            summary = "Obtener todas las tarjetas asociadas a un CVU",
            description = """
                    Devuelve la lista completa de tarjetas (crédito o débito) asociadas a una cuenta virtual (CVU).
                    Si la cuenta no tiene tarjetas, devuelve una lista vacía con estado 200.
                    """,
            parameters = {
                    @Parameter(name = "cvu", description = "Número de CVU de la cuenta", example = "0001234500006789012345")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarjetas obtenidas correctamente"),
                    @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    @GetMapping("/{cvu}/cards")
    public ResponseEntity<List<CardResponseDTO>> getCards(@PathVariable String cvu) {
        return ResponseEntity.ok(cardService.getCardsByCvu(cvu));
    }

    // 🔹 Obtener una tarjeta específica
    @Operation(
            summary = "Obtener una tarjeta específica",
            description = "Devuelve la información detallada de una tarjeta específica asociada al CVU indicado.",
            parameters = {
                    @Parameter(name = "cvu", description = "Número de CVU de la cuenta", example = "0001234500006789012345"),
                    @Parameter(name = "cardId", description = "Identificador único de la tarjeta", example = "1a2b3c4d-5678-90ef-1234-56789abcde00")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarjeta encontrada correctamente"),
                    @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada o no asociada al CVU"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
        @GetMapping("/{cvu}/{cardId}")
        public ResponseEntity<CardResponseDTO> getCard(
                        @PathVariable("cardId") String cardId,
                        @PathVariable("cvu") String cvu
                        ) {
                return ResponseEntity.ok(cardService.getCardById(cardId, cvu));
        }

    // 🔹 Agregar una nueva tarjeta
    @Operation(
            summary = "Agregar una nueva tarjeta a un CVU",
            description = """
                    Registra una nueva tarjeta (crédito o débito) y la asocia a la cuenta virtual (CVU) indicada.
                    La tarjeta debe ser válida y no estar ya asociada a otra cuenta.
                    """,
            parameters = {
                    @Parameter(name = "cvu", description = "Número de CVU de la cuenta a asociar", example = "0001234500006789012345")
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Datos de la tarjeta a registrar",
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
                    @ApiResponse(responseCode = "201", description = "Tarjeta creada y asociada correctamente"),
                    @ApiResponse(responseCode = "400", description = "Datos inválidos o formato incorrecto"),
                    @ApiResponse(responseCode = "409", description = "La tarjeta ya está asociada a otra cuenta"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    @PostMapping("/{cvu}")
    public ResponseEntity<CardResponseDTO> addCard(
            @PathVariable String cvu,
            @RequestBody CardRequestDTO request) {
        request.setAccountId(cvu);
        return ResponseEntity.status(HttpStatus.CREATED).body(cardService.addCard(request));
    }

    // 🔹 Eliminar una tarjeta
    @Operation(
            summary = "Eliminar una tarjeta",
            description = "Elimina una tarjeta asociada al CVU indicado. Se requiere el ID de la tarjeta.",
            parameters = {
                    @Parameter(name = "cvu", description = "Número de CVU de la cuenta", example = "0001234500006789012345"),
                    @Parameter(name = "cardId", description = "Identificador único de la tarjeta", example = "1a2b3c4d-5678-90ef-1234-56789abcde00")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Tarjeta eliminada correctamente"),
                    @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada"),
                    @ApiResponse(responseCode = "500", description = "Error interno del servidor")
            }
    )
    @DeleteMapping("/{cvu}/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable String cvu,
            @PathVariable String cardId) {
        cardService.deleteCard(cvu, cardId);
        return ResponseEntity.ok().build();
    }
}
