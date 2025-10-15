package com.backend.transactions_service.service;

import com.backend.transactions_service.exception.ResourceNotFoundException;
import com.backend.transactions_service.exception.ValidationException;
import com.backend.transactions_service.model.domain.Transaction;
import com.backend.transactions_service.model.domain.TransactionType;
import com.backend.transactions_service.model.dto.TransactionRequestDTO;
import com.backend.transactions_service.model.dto.TransactionResponseDTO;
import com.backend.transactions_service.repository.TransactionRepository;
import com.backend.transactions_service.client.AccountsClient;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Log4j2
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountsClient accountsClient;

    public TransactionService(TransactionRepository transactionRepository, AccountsClient accountsClient) {
        this.transactionRepository = transactionRepository;
        this.accountsClient = accountsClient;
    }

    // =============================================================
    // 🔹 Últimas 5 transacciones
    // =============================================================
    public List<TransactionResponseDTO> findLast5ByAccountId(String accountId) {
        validateAccountId(accountId);
        return transactionRepository.findLast5ByAccountId(accountId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // =============================================================
    // 🔹 Todas las transacciones
    // =============================================================
    public List<TransactionResponseDTO> findAllByAccountId(String accountId) {
        validateAccountId(accountId);
        return transactionRepository.findByAccountId(accountId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // =============================================================
    // 💳 Registrar depósito
    // =============================================================
    public TransactionResponseDTO registerDeposit(String accountId, TransactionRequestDTO dto) {
        validateDeposit(dto);

        Transaction tx = new Transaction();
        tx.setAccountId(accountId);
        tx.setAmount(dto.getAmount());
        tx.setDated(LocalDateTime.now());
        tx.setDescription(dto.getDescription() != null ? dto.getDescription() : "Depósito");
        tx.setOrigin(dto.getOrigin() != null ? dto.getOrigin() : "TARJETA");
        tx.setDestination(dto.getDestination() != null ? dto.getDestination() : accountId);
        tx.setCardId(dto.getCardId());
        tx.setType(TransactionType.DEPOSIT);

        Transaction saved = transactionRepository.save(tx);
        log.info("✅ Depósito registrado: cuenta={} monto=${}", accountId, dto.getAmount());
        return toResponseDTO(saved);
    }

    // =============================================================
    // 🔁 Registrar transferencia (DEBIT + CREDIT)
    // =============================================================
    public TransactionResponseDTO transfer(TransactionRequestDTO dto) {
        if (dto == null) throw new ValidationException("Los datos de la transferencia son obligatorios");

        String origin = dto.getAccountId();
        String destination = dto.getDestination();
        Double amount = dto.getAmount();

        if (origin == null || destination == null || amount == null) {
            throw new ValidationException("Datos incompletos para la transferencia");
        }
        if (origin.equals(destination)) {
            throw new ValidationException("No se puede transferir a la misma cuenta");
        }
        if (amount <= 0) {
            throw new ValidationException("El monto debe ser mayor que cero");
        }

        // 1️⃣ DEBIT: salida de fondos
        Transaction debitTx = new Transaction();
        debitTx.setAccountId(origin);
        debitTx.setAmount(amount);
        debitTx.setDated(LocalDateTime.now());
        debitTx.setDescription(dto.getDescription() != null ? dto.getDescription() : "Transferencia enviada");
        debitTx.setOrigin(origin);
        debitTx.setDestination(destination);
        debitTx.setType(TransactionType.DEBIT);
    Transaction savedDebit = transactionRepository.save(debitTx);

        // 2️⃣ CREDIT: entrada de fondos
        Transaction creditTx = new Transaction();
        creditTx.setAccountId(destination);
        creditTx.setAmount(amount);
        creditTx.setDated(LocalDateTime.now());
        creditTx.setDescription(dto.getDescription() != null ? dto.getDescription() : "Transferencia recibida");
        creditTx.setOrigin(origin);
        creditTx.setDestination(destination);
        creditTx.setType(TransactionType.CREDIT);
    Transaction savedCredit = transactionRepository.save(creditTx);

        log.info("✅ Transferencia registrada correctamente: {} → {} por ${}", origin, destination, amount);
        return toResponseDTO(savedDebit);
    }

    // =============================================================
    // 🔹 Actividad completa
    // =============================================================
    public List<TransactionResponseDTO> getAllTransactions(String accountId) {
        validateAccountId(accountId);
        return transactionRepository.findByAccountIdOrderByDatedDesc(accountId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // =============================================================
    // 🔹 Transacción específica
    // =============================================================
    public TransactionResponseDTO getTransactionByIdAndAccountId(String accountId, String id) {
        Transaction tx = transactionRepository.findByIdAndAccountId(id, accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada para la cuenta indicada"));
        return toResponseDTO(tx);
    }

    // =============================================================
    // 🔧 Validaciones y helpers
    // =============================================================
    private void validateAccountId(String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new ValidationException("El accountId es obligatorio");
        }
        if (!isValidCvu(accountId)) {
            throw new ValidationException("El formato del CVU es inválido");
        }
    }

    private void validateDeposit(TransactionRequestDTO dto) {
        if (dto == null) throw new ValidationException("Los datos del depósito son obligatorios");
        if (dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new ValidationException("El monto del depósito debe ser mayor a 0");
        }
        if (dto.getAccountId() == null || dto.getAccountId().trim().isEmpty()) {
            throw new ValidationException("El accountId es obligatorio");
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
        dto.setCardId(tx.getCardId());
        dto.setType(tx.getType());
        return dto;
    }

    

    private boolean isValidCvu(String cvu) {
        return cvu != null && cvu.trim().matches("\\d{22}");
    }
}
