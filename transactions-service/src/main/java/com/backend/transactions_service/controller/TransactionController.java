package com.backend.transactions_service.controller;

import com.backend.transactions_service.model.dto.TransactionRequestDTO;
import com.backend.transactions_service.model.dto.TransactionResponseDTO;
import com.backend.transactions_service.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Obtener los últimos 5 movimientos de una cuenta (por CVU)
     */
    @GetMapping("/accounts/{cvu}")
    public ResponseEntity<List<TransactionResponseDTO>> getLast5(@PathVariable String cvu) {
        return ResponseEntity.ok(transactionService.getLast5Transactions(cvu));
    }

    /**
     * Crear nueva transacción asociada a un CVU
     */
    @PostMapping
    public ResponseEntity<TransactionResponseDTO> createTransaction(
            @RequestBody TransactionRequestDTO request) {
        return ResponseEntity.ok(transactionService.saveTransaction(request));
    }
}
