package com.backend.accounts_service.client;

import com.backend.accounts_service.model.dto.CardRequestDTO;
import com.backend.accounts_service.model.dto.CardResponseDTO;
import com.backend.accounts_service.model.dto.CardSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@FeignClient(name = "CARDS-SERVICE")
public interface CardsClient {

    /**
     * Obtener todas las tarjetas asociadas a un CVU.
     */
    @GetMapping("/cards/{cvu}/cards")
    List<CardSummaryDTO> getCardsByAccount(@PathVariable("cvu") String cvu);

    /**
     * Agregar nueva tarjeta asociada a un CVU.
     */
    @PostMapping("/cards/{cvu}")
    CardResponseDTO addCard(@PathVariable("cvu") String cvu, @RequestBody CardRequestDTO request);


    // 🔹 Nuevo método para obtener una tarjeta específica
    @GetMapping("/cards/{cvu}/{cardId}")
    CardResponseDTO getCardById(@PathVariable("cvu") String cvu,@PathVariable("cardId") String cardId);

    // 🔹 Nuevo endpoint DELETE
    @DeleteMapping("/cards/{cvu}/{cardId}")
    void deleteCard(@PathVariable("cvu") String cvu,@PathVariable("cardId") String cardId);

}
