package com.backend.transactions_service.service;

import com.backend.transactions_service.client.AccountsClient;
import com.backend.transactions_service.model.domain.Transaction;
import com.backend.transactions_service.model.domain.TransactionType;
import com.backend.transactions_service.model.dto.TransactionRequestDTO;
import com.backend.transactions_service.model.dto.TransactionResponseDTO;
import com.backend.transactions_service.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountsClient accountsClient;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountsClient accountsClient) {
        this.transactionRepository = transactionRepository;
        this.accountsClient = accountsClient;
    }

    /**
     * Obtener últimos 5 movimientos por CVU
     */
    public List<TransactionResponseDTO> findLast5ByCvu(String cvu) {
        return transactionRepository.findLast5ByCvu(cvu)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    /**
     * Buscar todas las transacciones asociadas a un CVU
     */
    public List<TransactionResponseDTO> findAllByCvu(String cvu) {
        List<Transaction> transactions = transactionRepository.findByAccountCvu(cvu);

        return transactions.stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }



    /**
     * Crear y guardar una transacción para un CVU,
     * y actualizar el balance en accounts-service.
     */
    public TransactionResponseDTO saveTransaction(TransactionRequestDTO dto) {
        Transaction transaction = new Transaction();
        transaction.setAccountId(dto.getAccountId()); // acá accountId = CVU
        transaction.setAmount(dto.getAmount());
        transaction.setDated(dto.getDated() != null ? dto.getDated() : LocalDateTime.now());
        transaction.setDescription(dto.getDescription());
        transaction.setOrigin(dto.getOrigin());
        transaction.setDestination(dto.getDestination());
        transaction.setType(dto.getType()); // convierte String -> Enum

        Transaction saved = transactionRepository.save(transaction);

        // Llamada a accounts-service para actualizar balance
        accountsClient.updateBalance(saved.getAccountId(), saved.getAmount(), saved.getType().name());

        return toResponseDTO(saved);
    }

    // ---------- Helpers ----------

    private TransactionType parseType(String raw) {
        if (raw == null) {
            throw new IllegalArgumentException("El campo 'type' es obligatorio (CREDIT|DEBIT|TRANSFER).");
        }
        try {
            return TransactionType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException(
                    "Tipo de transacción inválido: " + raw + " (esperado: CREDIT|DEBIT|TRANSFER)"
            );
        }
    }

    private TransactionResponseDTO toResponseDTO(Transaction tx) {
        TransactionResponseDTO dto = new TransactionResponseDTO();
        dto.setId(tx.getId());
        dto.setAccountId(tx.getAccountId());
        dto.setAmount(tx.getAmount());
        dto.setDated(tx.getDated());
        dto.setDescription(tx.getDescription());
        dto.setOrigin(tx.getOrigin());
        dto.setDestination(tx.getDestination());
        dto.setType(tx.getType()); // Enum -> String
        return dto;
    }
}
