package com.backend.cards_service.repository;

import com.backend.cards_service.model.domain.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, String> {
    List<Card> findByAccountId(String accountId);
}
