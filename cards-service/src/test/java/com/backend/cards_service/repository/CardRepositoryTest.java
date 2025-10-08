package com.backend.cards_service.repository;

import com.backend.cards_service.model.domain.Card;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.ANY)
class CardRepositoryTest {
    @Autowired
    private CardRepository cardRepository;

    @Test
    void saveAndFindCard() {
        Card card = new Card();
        // required fields (entity has non-null constraints)
        card.setAccountId("test-account");
        card.setType("DEBIT");
        card.setNumber("4111111111111111");
        card.setProvider("VISA");
        card.setExpiration("12/30");

        Card saved = cardRepository.save(card);
        assertNotNull(saved.getId());
        assertTrue(cardRepository.findById(saved.getId()).isPresent());
    }
}
