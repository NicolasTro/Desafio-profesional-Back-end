package com.backend.cards_service.controller;

import com.backend.cards_service.model.dto.CardRequestDTO;
import com.backend.cards_service.model.dto.CardResponseDTO;
import com.backend.cards_service.service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cards")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    /**
     * Obtener todas las tarjetas asociadas a un CVU
     */
    @GetMapping("/{cvu}/cards")
    public ResponseEntity<List<CardResponseDTO>> getCards(@PathVariable String cvu) {
        return ResponseEntity.ok(cardService.getCardsByCvu(cvu));
    }

    /**
     * Obtener una tarjeta específica por CVU + cardId
     */
    @GetMapping("/{cvu}/{cardId}")
    public ResponseEntity<CardResponseDTO> getCard(
            @PathVariable String cvu,
            @PathVariable String cardId) {
        return ResponseEntity.ok(cardService.getCardById(cvu, cardId));
    }

    /**
     * Agregar nueva tarjeta a un CVU
     */
    @PostMapping("/{cvu}")
    public ResponseEntity<CardResponseDTO> addCard(
            @PathVariable String cvu,
            @RequestBody CardRequestDTO request) {
        request.setAccountId(cvu); // asociamos tarjeta al CVU
        return ResponseEntity.ok(cardService.addCard(request));
    }

    /**
     * Eliminar tarjeta por CVU + cardId
     */
    @DeleteMapping("/{cvu}/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable String cvu,
            @PathVariable String cardId) {
        cardService.deleteCard(cvu, cardId);
        return ResponseEntity.status(HttpStatus.OK).build();
//                .ok().build();
    }
}
