package com.backend.transactions_service.service;

import com.backend.transactions_service.client.AccountsClient;
import com.backend.transactions_service.model.domain.Transaction;
import com.backend.transactions_service.model.domain.TransactionType;
import com.backend.transactions_service.model.dto.TransactionRequestDTO;
import com.backend.transactions_service.model.dto.TransactionResponseDTO;
import com.backend.transactions_service.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountsClient accountsClient;

    @InjectMocks
    private TransactionService transactionService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void saveTransaction_shouldSaveAndCallAccountsClient() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
    dto.setAccountId("1234567890123456789012");
    dto.setAmount(100.0);
    dto.setType(TransactionType.CREDIT);
        dto.setDescription("desc");
    dto.setOrigin("1234567890123456789012");
    dto.setDestination("1234567890123456789013");
        dto.setDated(LocalDateTime.now());

        Transaction saved = new Transaction();
        saved.setId("id-1");
        saved.setAccountId(dto.getAccountId());
        saved.setAmount(dto.getAmount());
        saved.setType(TransactionType.CREDIT);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponseDTO result = transactionService.saveTransaction(dto);

        assertNotNull(result);
        assertEquals("id-1", result.getId());
        verify(transactionRepository).save(any(Transaction.class));
        verify(accountsClient).updateBalance(eq(saved.getAccountId()), eq(saved.getAmount()), anyString());
    }

    @Test
    void findAllByAccountId_shouldReturnList() {
        Transaction tx = new Transaction();
        tx.setId("t1");
    when(transactionRepository.findByAccountId("1234567890123456789012")).thenReturn(Collections.singletonList(tx));

    List<TransactionResponseDTO> result = transactionService.findAllByAccountId("1234567890123456789012");

        assertEquals(1, result.size());
        assertEquals("t1", result.get(0).getId());
    }

    @Test
    void findLast5ByAccountId_shouldReturnList() {
        Transaction tx = new Transaction();
        tx.setId("t2");
    when(transactionRepository.findLast5ByAccountId("1234567890123456789012")).thenReturn(Collections.singletonList(tx));

    List<TransactionResponseDTO> result = transactionService.findLast5ByAccountId("1234567890123456789012");

        assertEquals(1, result.size());
        assertEquals("t2", result.get(0).getId());
    }
}
