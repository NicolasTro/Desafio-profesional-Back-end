package com.backend.cards_service.service;

import com.backend.cards_service.model.domain.Card;
import com.backend.cards_service.model.dto.CardRequestDTO;
import com.backend.cards_service.model.dto.CardResponseDTO;
import com.backend.cards_service.repository.CardRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CardServiceTest {
    @Mock
    private CardRepository cardRepository;

    @InjectMocks
    private CardService cardService;

    public CardServiceTest() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
        void addCard_shouldSaveAndReturnResponse() {
            CardRequestDTO request = new CardRequestDTO();
            request.setAccountId("test-account");
            request.setCardNumber("4111111111111111");
            request.setExpiration("12/30");
            request.setType("DEBIT");
            Card card = new Card();
            when(cardRepository.save(any(Card.class))).thenReturn(card);
            when(cardRepository.findByNumber(anyString())).thenReturn(Optional.empty());
            CardResponseDTO response = cardService.addCard(request);
            assertNotNull(response);
    }

        @Test
        void getCardById_shouldReturnCard() {
            String cvu = "test-cvu";
            String cardId = "test-id";
            Card card = new Card();
            when(cardRepository.findByIdAndAccountId(cvu, cardId)).thenReturn(Optional.of(card));
            CardResponseDTO result = cardService.getCardById(cvu, cardId);
            assertNotNull(result);
    }
}
