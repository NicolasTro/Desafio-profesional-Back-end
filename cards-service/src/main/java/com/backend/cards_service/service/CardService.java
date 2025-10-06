package com.backend.cards_service.service;

import com.backend.cards_service.exception.CardNotFound;
import com.backend.cards_service.exception.CardAlreadyExistsInAccountException;
import com.backend.cards_service.exception.CardAlreadyLinkedToAnotherAccountException;
import com.backend.cards_service.exception.InvalidCardDataException;

import com.backend.cards_service.model.domain.Card;
import com.backend.cards_service.model.dto.CardRequestDTO;
import com.backend.cards_service.model.dto.CardResponseDTO;
import com.backend.cards_service.repository.CardRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Log4j2
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
        Optional<Card> card = cardRepository.findByIdAndAccountId(cvu,cardId);

        log.info(card);
        if (card.isEmpty()) {
            throw new CardNotFound("Tarjeta no encontrada");
        }


        return toResponseDTO(card.get());
    }

    /**
     * Agregar una tarjeta asociada a un CVU (accountId)
     */
    public CardResponseDTO addCard(CardRequestDTO dto) {
        // 🔹 Validar campos obligatorios

        log.info(dto);

        if (dto.getCardNumber() == null || dto.getCardNumber().isBlank()) {
            throw new InvalidCardDataException("El número de tarjeta no puede ser nulo o vacío.");
        }

        if (dto.getExpiration() == null || dto.getExpiration().isBlank()) {
            throw new InvalidCardDataException("La fecha de expiración es obligatoria.");
        }

        if (dto.getAccountId() == null || dto.getAccountId().isBlank()) {
            throw new InvalidCardDataException("El ID de cuenta es obligatorio.");
        }

        // 🔹 Validar formato y fecha de expiración
        if (!isValidExpiry(dto.getExpiration())) {
            throw new InvalidCardDataException("La fecha de expiración es inválida o ya venció.");
        }

        // 🔹 Normalizar el número (por si vienen espacios)
        String cardNumber = dto.getCardNumber().replaceAll("\\s+", "");
        log.info("cardNumber = " + cardNumber);
        if (cardNumber.length() < 13) {
            throw new InvalidCardDataException("El número de tarjeta es demasiado corto o inválido.");
        }

        // 🔹 Verificar duplicados globales (por número completo)
        Optional<Card> existingCard = cardRepository.findByNumber(cardNumber);
        if (existingCard.isPresent()) {
            Card found = existingCard.get();
            if (found.getAccountId().equals(dto.getAccountId())) {
                throw new CardAlreadyExistsInAccountException("La tarjeta ya está registrada en esta cuenta.");
            } else {
                throw new CardAlreadyLinkedToAnotherAccountException("La tarjeta ya está asociada a otra cuenta.");
            }
        }

        // 🔹 Calcular proveedor según primer dígito
        char firstDigit = cardNumber.charAt(0);
        String provider = switch (firstDigit) {
            case '4' -> "VISA";
            case '5' -> "MASTERCARD";
            case '3' -> "AMEX";
            case '6' -> "DISCOVER";
            default -> "DESCONOCIDO";
        };

        // 🔹 Crear entidad (guardamos el número completo)
        Card card = new Card();
        card.setAccountId(dto.getAccountId());
        card.setType(dto.getType());
        card.setNumber(cardNumber); // se guarda completo
        card.setProvider(provider);
        card.setExpiration(dto.getExpiration());

        Card saved = cardRepository.save(card);

        // 🔹 Crear response con número enmascarado
        CardResponseDTO response = toResponseDTO(saved);
        response.setNumberMasked("**** **** **** " + cardNumber.substring(cardNumber.length() - 4));

        return response;
    }


    /**
     * Validar que la expiración esté en formato MM/YY y sea futura
     */
    private boolean isValidExpiry(String expiration) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yy");
            YearMonth expiry = YearMonth.parse(expiration, formatter);
            YearMonth now = YearMonth.now();
            return expiry.isAfter(now);
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * Eliminar tarjeta por CVU + cardId
     */
    public void deleteCard(String cvu, String cardId) {
        Optional<Card> card = cardRepository.findById(cardId);
        if (card.isEmpty()) {
            throw new RuntimeException("Tarjeta no encontrada");
        }


        cardRepository.delete(card.get());
    }

    // ---------- Helpers ----------

    private CardResponseDTO toResponseDTO(Card card) {
        CardResponseDTO dto = new CardResponseDTO();
        dto.setId(card.getId());
        dto.setAccountId(card.getAccountId());
        dto.setType(card.getType());
        dto.setNumberMasked(card.getNumber());
        dto.setProvider(card.getProvider());
        dto.setExpiration(card.getExpiration());
        return dto;
    }
}
