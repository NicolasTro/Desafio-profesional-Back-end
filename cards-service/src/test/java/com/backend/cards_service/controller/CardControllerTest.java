package com.backend.cards_service.controller;

import com.backend.cards_service.model.dto.CardRequestDTO;
import com.backend.cards_service.model.dto.CardResponseDTO;
import com.backend.cards_service.service.CardService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardControllerTest {
    @Mock
    private CardService cardService;

    @InjectMocks
    private CardController cardController;

    public CardControllerTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
        void addCard_shouldReturnResponse() {
            String cvu = "test-cvu";
            CardRequestDTO request = new CardRequestDTO();
            CardResponseDTO response = new CardResponseDTO();
            when(cardService.addCard(any(CardRequestDTO.class))).thenReturn(response);
            // Simular el endpoint: addCard(String cvu, CardRequestDTO request)
            request.setAccountId(cvu);
            ResponseEntity<CardResponseDTO> result = cardController.addCard(cvu, request);
            assertEquals(response, result.getBody());
    }
}
