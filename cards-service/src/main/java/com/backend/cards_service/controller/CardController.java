package com.backend.cards_service.controller;

import com.backend.cards_service.model.dto.CardRequestDTO;
import com.backend.cards_service.model.dto.CardResponseDTO;
import com.backend.cards_service.service.CardService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts/{cvu}/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * Obtener todas las tarjetas asociadas a un CVU
     */
    @GetMapping
    public ResponseEntity<List<CardResponseDTO>> getCards(@PathVariable String cvu) {
        return ResponseEntity.ok(cardService.getCardsByCvu(cvu));
    }

    /**
     * Obtener una tarjeta específica por CVU + cardId
     */
    @GetMapping("/{cardId}")
    public ResponseEntity<CardResponseDTO> getCard(
            @PathVariable String cvu,
            @PathVariable String cardId) {
        return ResponseEntity.ok(cardService.getCardById(cvu, cardId));
    }

    /**
     * Agregar nueva tarjeta a un CVU
     */
    @PostMapping
    public ResponseEntity<CardResponseDTO> addCard(
            @PathVariable String cvu,
            @RequestBody CardRequestDTO request) {
        request.setAccountId(cvu); // asociamos tarjeta al CVU
        return ResponseEntity.ok(cardService.addCard(request));
    }

    /**
     * Eliminar tarjeta por CVU + cardId
     */
    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable String cvu,
            @PathVariable String cardId) {
        cardService.deleteCard(cvu, cardId);
        return ResponseEntity.ok().build();
    }
}
