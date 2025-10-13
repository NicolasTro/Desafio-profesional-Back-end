package com.backend.transactions_service.service;

import com.backend.transactions_service.client.AccountsClient;
import com.backend.transactions_service.exception.ForbiddenException;
import com.backend.transactions_service.exception.ValidationException;
import com.backend.transactions_service.model.domain.Transaction;
import com.backend.transactions_service.model.domain.TransactionType;
import com.backend.transactions_service.model.dto.TransactionRequestDTO;
import com.backend.transactions_service.model.dto.TransactionResponseDTO;
import com.backend.transactions_service.repository.TransactionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service

@Log4j2
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountsClient accountsClient;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountsClient accountsClient) {
        this.transactionRepository = transactionRepository;
        this.accountsClient = accountsClient;
    }

    // =============================================================
    // 🔹 Obtener últimas 5 transacciones
    // =============================================================
    public List<TransactionResponseDTO> findLast5ByAccountId(String accountId) {
        validateAccountId(accountId);
    // Use compatibility method so tests that stub findLast5ByAccountId(...) work correctly
    return transactionRepository.findLast5ByAccountId(accountId)
        .stream()
        .map(this::toResponseDTO)
        .toList();
    }

    // =============================================================
    // 🔹 Obtener todas las transacciones
    // =============================================================
    public List<TransactionResponseDTO> findAllByAccountId(String accountId) {
        validateAccountId(accountId);
        List<Transaction> transactions = transactionRepository.findByAccountId(accountId);
        return transactions.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // =============================================================
    // 🔹 Crear transacción general
    // =============================================================
    public TransactionResponseDTO saveTransaction(TransactionRequestDTO dto) {
        validateTransactionRequest(dto);
        validateTransactionBusiness(dto);

        Transaction transaction = new Transaction();
        transaction.setAccountId(dto.getAccountId());
        transaction.setAmount(dto.getAmount());
        transaction.setDated(dto.getDated() != null ? dto.getDated() : LocalDateTime.now());
        transaction.setDescription(dto.getDescription());
        transaction.setOrigin(dto.getOrigin());
        transaction.setDestination(dto.getDestination());
    transaction.setCardId(dto.getCardId());
        transaction.setType(dto.getType());

        Transaction saved = transactionRepository.save(transaction);

        try {
            accountsClient.updateBalance(saved.getAccountId(), saved.getAmount(), saved.getType().name());
        } catch (Exception e) {
            throw new ForbiddenException("No se pudo actualizar el balance de la cuenta. Verifique los permisos o conectividad.");
        }

        return toResponseDTO(saved);
    }

    // =============================================================
    // 🆕 Registrar ingreso de dinero (Depósito)
    // =============================================================
    public TransactionResponseDTO registerDeposit(String accountId, TransactionRequestDTO dto) {
        // Validar parámetros mínimos
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new ValidationException("El accountId es obligatorio");
        }
        if (dto == null || dto.getAmount() == null || dto.getAmount() <= 0) {
            throw new ValidationException("El monto del depósito es obligatorio y debe ser mayor a 0");
        }
        log.info("entro al registerDeposit");
        log.info(dto);

        // Crear entidad Transaction
        Transaction transaction = new Transaction();
        transaction.setAccountId(accountId);
        transaction.setAmount(dto.getAmount());
        transaction.setDated(LocalDateTime.now());
        transaction.setDescription(dto.getDescription() != null ? dto.getDescription() : "Depósito");
        transaction.setOrigin(dto.getOrigin() != null ? dto.getOrigin() : "EXTERNAL_SOURCE");
        transaction.setDestination(dto.getDestination() != null ? dto.getDestination() : accountId);
    transaction.setCardId(dto.getCardId());
        transaction.setType(TransactionType.DEPOSIT);

        Transaction saved = transactionRepository.save(transaction);

        try {
        // Actualizar balance en accounts-service
//            accountsClient.updateBalance(accountId, saved.getAmount(), saved.getType().name());
        } catch (Exception e) {
            throw new ForbiddenException("No se pudo actualizar el balance de la cuenta durante el depósito."+ e.getMessage());
        }

        return toResponseDTO(saved);
    }

    // =============================================================
    // 🔹 Obtener todas las transacciones (actividad completa)
    // =============================================================
    public List<TransactionResponseDTO> getAllTransactions(String accountId) {
        validateAccountId(accountId);
        List<Transaction> transactions = transactionRepository.findByAccountIdOrderByDatedDesc(accountId);
        return transactions.stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // =============================================================
    // 🔹 Obtener una transacción específica
    // =============================================================
    public TransactionResponseDTO getTransactionByIdAndAccountId(String accountId, String id) {
    Transaction transaction = transactionRepository.findByIdAndAccountId(id, accountId)
        .orElseThrow(() -> new com.backend.transactions_service.exception.ResourceNotFoundException("Transacción no encontrada para el accountId proporcionado"));
        return toResponseDTO(transaction);
    }

    // =============================================================
    // 🔹 Métodos de validación y helpers
    // =============================================================
    private void validateAccountId(String accountId) {
        if (accountId == null || accountId.trim().isEmpty()) {
            throw new ValidationException("El accountId es obligatorio y no puede estar vacío");
        }
        if (!isValidCvu(accountId)) {
            throw new ValidationException("El formato del CVU es inválido");
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

    // =============================================================
    // 🔹 Validaciones reutilizadas del flujo general
    // =============================================================
    private void validateTransactionRequest(TransactionRequestDTO dto) {
        if (dto == null) throw new ValidationException("Los datos de la transacción son obligatorios");
        if (dto.getAccountId() == null || dto.getAccountId().trim().isEmpty()) {
            throw new ValidationException("El accountId es obligatorio");
        }
        if (dto.getAmount() == null) {
            throw new ValidationException("El monto es obligatorio");
        }
        if (dto.getType() == null) {
            throw new ValidationException("El tipo de transacción es obligatorio");
        }
        if (dto.getDescription() == null || dto.getDescription().trim().isEmpty()) {
            throw new ValidationException("La descripción es obligatoria");
        }
    }

    private void validateTransactionBusiness(TransactionRequestDTO dto) {
        validateCvuFormat(dto.getAccountId());
        validateAmount(dto.getAmount());
        validateTransactionType(dto.getType());
        validateTransactionFields(dto);
        validateDescriptionLength(dto.getDescription());
        validateCvuFields(dto);
    }

    private void validateCvuFormat(String accountId) {
        if (!isValidCvu(accountId)) {
            throw new ValidationException("El formato del CVU es inválido");
        }
    }

    private void validateAmount(Double amount) {
        if (amount <= 0) {
            throw new ValidationException("El monto debe ser mayor a cero");
        }
        if (amount > 1_000_000.0) {
            throw new ForbiddenException("El monto excede el límite máximo permitido por transacción");
        }
    }

    private void validateTransactionType(TransactionType type) {
        if (type != TransactionType.CREDIT && type != TransactionType.DEBIT && type != TransactionType.DEPOSIT) {
            throw new ValidationException("Tipo de transacción inválido: " + type);
        }
    }

    private void validateTransactionFields(TransactionRequestDTO dto) {
        if (dto.getType() == TransactionType.DEBIT &&
                (dto.getOrigin() == null || dto.getOrigin().trim().isEmpty())) {
            throw new ValidationException("El origen es obligatorio para transacciones de débito");
        }

        if (dto.getType() == TransactionType.CREDIT &&
                (dto.getDestination() == null || dto.getDestination().trim().isEmpty())) {
            throw new ValidationException("El destino es obligatorio para transacciones de crédito");
        }
    }

    private void validateDescriptionLength(String description) {
        if (description.length() > 255) {
            throw new ValidationException("La descripción no puede exceder 255 caracteres");
        }
    }

    private void validateCvuFields(TransactionRequestDTO dto) {
        if (dto.getOrigin() != null && !dto.getOrigin().trim().isEmpty() && !isValidCvu(dto.getOrigin())) {
            throw new ValidationException("El formato del CVU de origen es inválido");
        }

        if (dto.getDestination() != null && !dto.getDestination().trim().isEmpty() && !isValidCvu(dto.getDestination())) {
            throw new ValidationException("El formato del CVU de destino es inválido");
        }
    }

    private boolean isValidCvu(String cvu) {
        if (cvu == null || cvu.trim().isEmpty()) return false;
        String cleanCvu = cvu.trim();
        return cleanCvu.matches("\\d{22}");
    }
}
