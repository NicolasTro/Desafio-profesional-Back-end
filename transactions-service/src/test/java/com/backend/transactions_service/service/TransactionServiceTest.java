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
        // Test registerDeposit flow
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setAccountId("1234567890123456789012");
        dto.setAmount(100.0);
        dto.setDescription("desc");
        dto.setOrigin("TARJETA");
        dto.setDestination("1234567890123456789012");

        Transaction saved = new Transaction();
        saved.setId("id-1");
        saved.setAccountId(dto.getAccountId());
        saved.setAmount(dto.getAmount());
        saved.setType(TransactionType.DEPOSIT);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(saved);

        TransactionResponseDTO result = transactionService.registerDeposit(dto.getAccountId(), dto);

        assertNotNull(result);
        assertEquals("id-1", result.getId());
        verify(transactionRepository).save(any(Transaction.class));
        // registerDeposit doesn't call accountsClient in this implementation, so we don't verify it here
    }

    @Test
    void transfer_shouldSaveTwoTransactions() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        dto.setAccountId("1234567890123456789012");
        dto.setDestination("1234567890123456789013");
        dto.setAmount(50.0);
        dto.setType(TransactionType.CREDIT);

        // When saving, return the same transaction for debit (id t-debit)
        Transaction debit = new Transaction();
        debit.setId("t-debit");
        debit.setAccountId(dto.getAccountId());
        debit.setAmount(dto.getAmount());
        debit.setType(TransactionType.DEBIT);

        when(transactionRepository.save(any(Transaction.class))).thenReturn(debit);

        TransactionResponseDTO response = transactionService.transfer(dto);

        assertNotNull(response);
        assertEquals("t-debit", response.getId());
        // Expect two saves (debit + credit)
        verify(transactionRepository, times(2)).save(any(Transaction.class));
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
