package com.backend.transactions_service.controller;

import com.backend.transactions_service.model.domain.Transaction;
import com.backend.transactions_service.model.dto.TransactionResponseDTO;
import com.backend.transactions_service.model.dto.TransactionRequestDTO;
import com.backend.transactions_service.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TransactionControllerTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private TransactionController transactionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createTransaction_shouldReturnResponse() {
        TransactionRequestDTO dto = new TransactionRequestDTO();
        Transaction tx = new Transaction();
        tx.setId("tx1");
        when(transactionService.saveTransaction(any(TransactionRequestDTO.class))).thenReturn(null);

        ResponseEntity<TransactionResponseDTO> response = transactionController.createTransaction(dto);

        assertEquals(201, response.getStatusCodeValue());
        verify(transactionService).saveTransaction(dto);
    }

    @Test
    void getAllByAccount_shouldReturnList() {
        String cvu = "cvu123";
        when(transactionService.findAllByAccountId(cvu)).thenReturn(Collections.emptyList());

        ResponseEntity<List<TransactionResponseDTO>> resp = transactionController.getAllByAccount(cvu);

        assertEquals(200, resp.getStatusCodeValue());
        verify(transactionService).findAllByAccountId(cvu);
    }

    @Test
    void getLast5ByAccount_shouldReturnList() {
        String cvu = "cvu123";
        when(transactionService.findLast5ByAccountId(cvu)).thenReturn(Collections.emptyList());

        ResponseEntity<List<TransactionResponseDTO>> resp = transactionController.getLast5ByAccount(cvu);

        assertEquals(200, resp.getStatusCodeValue());
        verify(transactionService).findLast5ByAccountId(cvu);
    }
}
