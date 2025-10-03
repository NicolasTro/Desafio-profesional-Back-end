package com.backend.accounts_service.client;

import com.backend.accounts_service.model.dto.CardSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "cards-service", path = "/accounts")
public interface CardsClient {

    /**
     * Obtener todas las tarjetas asociadas a un CVU
     */
    @GetMapping("/{cvu}/cards")
    List<CardSummaryDTO> getCardsByAccount(@PathVariable("cvu") String cvu);
}
