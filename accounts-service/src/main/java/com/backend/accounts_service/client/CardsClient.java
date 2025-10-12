package com.backend.accounts_service.client;

import com.backend.accounts_service.model.dto.CardRequestDTO;
import com.backend.accounts_service.model.dto.CardResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "CARDS-SERVICE")
public interface CardsClient {

    /**
     * Obtener todas las tarjetas asociadas a un CVU.
     */
    @GetMapping("/cards/{cvu}/cards")
    List<CardResponseDTO> getCardsByAccount(@PathVariable("cvu") String cvu);

    /**
     * Agregar nueva tarjeta asociada a un CVU.
     */
    @PostMapping("/cards/{cvu}")
    CardResponseDTO addCard(@PathVariable("cvu") String cvu, @RequestBody CardRequestDTO request);

    /**
     * Obtener una tarjeta específica asociada a un CVU.
     */

    @GetMapping("/cards/{cvu}/{cardId}")
    CardResponseDTO getCardById(@PathVariable("cardId") String cardId, @PathVariable("cvu") String cvu);

    /**
     * Eliminar una tarjeta específica asociada a un CVU.
     */

    @DeleteMapping("/cards/{cvu}/{cardId}")
    void deleteCard(@PathVariable("cvu") String cvu, @PathVariable("cardId") String cardId);

}
