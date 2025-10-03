package com.backend.cards_service.service;

import com.backend.cards_service.model.domain.Card;
import com.backend.cards_service.model.dto.CardRequestDTO;
import com.backend.cards_service.model.dto.CardResponseDTO;
import com.backend.cards_service.repository.CardRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CardService {

    private final CardRepository cardRepository;

    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    /**
     * Obtener todas las tarjetas asociadas a un CVU
     */
    public List<CardResponseDTO> getCardsByCvu(String cvu) {
        return cardRepository.findByAccountId(cvu)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Obtener una tarjeta específica por CVU + cardId
     */
    public CardResponseDTO getCardById(String cvu, String cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));

        if (!card.getAccountId().equals(cvu)) {
            throw new RuntimeException("La tarjeta no pertenece al CVU " + cvu);
        }

        return toResponseDTO(card);
    }

    /**
     * Agregar una tarjeta asociada a un CVU
     */
    public CardResponseDTO addCard(CardRequestDTO dto) {
        // Validar duplicado
        cardRepository.findByAccountId(dto.getAccountId()).stream()
                .filter(c -> c.getNumberMasked().equals(dto.getNumberMasked()))
                .findAny()
                .ifPresent(c -> {
                    throw new RuntimeException("La tarjeta ya está asociada a esta cuenta");
                });

        Card card = new Card();
        card.setAccountId(dto.getAccountId()); // acá accountId = CVU
        card.setType(dto.getType());
        card.setNumberMasked(dto.getNumberMasked());
        card.setProvider(dto.getProvider());
        card.setExpiration(dto.getExpiration());

        return toResponseDTO(cardRepository.save(card));
    }

    /**
     * Eliminar tarjeta por CVU + cardId
     */
    public void deleteCard(String cvu, String cardId) {
        Card card = cardRepository.findById(cardId)
                .orElseThrow(() -> new RuntimeException("Tarjeta no encontrada"));

        if (!card.getAccountId().equals(cvu)) {
            throw new RuntimeException("La tarjeta no pertenece al CVU " + cvu);
        }

        cardRepository.delete(card);
    }

    // ---------- Helpers ----------

    private CardResponseDTO toResponseDTO(Card card) {
        CardResponseDTO dto = new CardResponseDTO();
        dto.setId(card.getId());
        dto.setAccountId(card.getAccountId());
        dto.setType(card.getType());
        dto.setNumberMasked(card.getNumberMasked());
        dto.setProvider(card.getProvider());
        dto.setExpiration(card.getExpiration());
        return dto;
    }
}
