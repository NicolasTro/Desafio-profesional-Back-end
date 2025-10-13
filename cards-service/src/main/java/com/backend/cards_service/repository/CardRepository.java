package com.backend.cards_service.repository;

import com.backend.cards_service.model.domain.Card;
import com.backend.cards_service.model.dto.CardResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, String> {
    List<Card> findByAccountId(String accountId);
    Optional<Card> findByNumber(String cardNumber);

    Optional<Card> findByIdAndAccountId(String id, String accountId);
}
