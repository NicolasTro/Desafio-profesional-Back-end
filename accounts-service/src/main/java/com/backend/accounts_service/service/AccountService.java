package com.backend.accounts_service.service;

import com.backend.accounts_service.client.TransactionsClient;
import com.backend.accounts_service.client.CardsClient;
import com.backend.accounts_service.exception.ResourceNotFoundException;
import com.backend.accounts_service.model.domain.Account;
import com.backend.accounts_service.model.dto.*;
import com.backend.accounts_service.repository.AccountRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

@Service
@Log4j2
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionsClient transactionsClient;
    private final CardsClient cardsClient;
    private final List<String> wordList;

    @Value("${internal.api.key}")
    private String internalApiKey;

    public AccountService(AccountRepository accountRepository,
                          TransactionsClient transactionsClient,
                          CardsClient cardsClient) {
        this.accountRepository = accountRepository;
        this.transactionsClient = transactionsClient;
        this.cardsClient = cardsClient;
        this.wordList = loadWords();
    }

    // =============================================================
    //  Crear cuenta
    // =============================================================
    public AccountResponseDTO createAccount(AccountCreateDTO dto) {
        Optional<Account> existing = accountRepository.findByUserId(dto.getUserId());
        if (existing.isPresent()) {
            throw new RuntimeException("El usuario ya tiene una cuenta asociada");
        }

        Account account = Account.builder()
                .userId(dto.getUserId())
                .cvu(generateCvu())
                .alias(generateAlias())
                .balance(0.0)
                .currency("ARS")
                .build();

        Account saved = accountRepository.save(account);
        return toResponseDTO(saved);
    }

    // =============================================================
    //  Obtener cuentas
    // =============================================================
    public AccountResponseDTO getAccountByCvu(String cvu) {
        Account account = accountRepository.findByCvu(cvu)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));
        return toResponseDTO(account);
    }

    public AccountResponseDTO getAccountByUserId(String userId) {
        Account account = accountRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));
        return toResponseDTO(account);
    }

    // =============================================================
    //  Actualizar cuenta (alias / currency)
    // =============================================================
    public AccountResponseDTO updateAccount(String cvu, AccountUpdateDTO dto) {
        Account account = accountRepository.findByCvu(cvu)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (dto.getAlias() != null) account.setAlias(dto.getAlias());
        if (dto.getCurrency() != null) account.setCurrency(dto.getCurrency());

        return toResponseDTO(accountRepository.save(account));
    }

    // =============================================================
    //  Gestión de tarjetas (cards-service)
    // =============================================================
    public List<CardResponseDTO> getCards(String cvu) {
        accountRepository.findByCvu(cvu)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        return cardsClient.getCardsByAccount(cvu);
    }

    public CardResponseDTO addCard(String cvu, CardRequestDTO request) {
        request.setAccountId(cvu);

        try {
            return cardsClient.addCard(cvu, request);
        } catch (feign.FeignException.BadRequest e) {
            throw new RuntimeException("Datos inválidos al registrar la tarjeta: " + e.contentUTF8());
        } catch (feign.FeignException.Conflict e) {
            throw new RuntimeException("La tarjeta ya está asociada a otra cuenta: " + e.contentUTF8());
        } catch (feign.FeignException.InternalServerError e) {
            throw new RuntimeException("Error interno en cards-service: " + e.contentUTF8());
        } catch (feign.FeignException e) {
            throw new RuntimeException("Error al comunicarse con cards-service: " + e.getMessage());
        }
    }

    public CardResponseDTO getCardById(String cardId, String cvu) {
        try {
            return cardsClient.getCardById(cardId, cvu);
        } catch (FeignException e) {
            String errorMessage = extractErrorMessage(e.contentUTF8());
            throw new ResponseStatusException(HttpStatus.valueOf(e.status()), errorMessage);
        }
    }

    public void deleteCard(String cvu, String cardId) {
        cardsClient.deleteCard(cvu, cardId);
    }

    // =============================================================
    //  Actualizar balance (usado desde transactions-service)
    // =============================================================
    public void updateBalance(String cvu, Double amount, String type) {
        Account account = accountRepository.findByCvu(cvu)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if ("CREDIT".equalsIgnoreCase(type)) {
            account.setBalance(account.getBalance() + amount);
        } else if ("DEBIT".equalsIgnoreCase(type)) {
            if (account.getBalance() < amount) {
                throw new RuntimeException("Saldo insuficiente");
            }
            account.setBalance(account.getBalance() - amount);
        } else {
            throw new RuntimeException("Tipo de transacción inválido: " + type);
        }

        accountRepository.save(account);
    }

    // =============================================================
    // Registrar ingreso de dinero (depósito)
    // =============================================================
    @Transactional(rollbackFor = Exception.class)
    public TransactionResponseDTO registerDeposit(String accountId, TransactionRequestDTO request) {

        Account account = accountRepository.findByCvu(accountId)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada"));

        Double originalBalance = account.getBalance() != null ? account.getBalance() : 0.0;

        try {
            // Validación: si el ingreso proviene de una tarjeta, el cardId es obligatorio
            if (request != null && "TARJETA".equalsIgnoreCase(request.getOrigin())) {
                if (request.getCardId() == null || request.getCardId().trim().isEmpty()) {
                    throw new com.backend.accounts_service.exception.ValidationException("El campo cardId es obligatorio cuando el origen es TARJETA");
                }
            }
            // 1️⃣ Actualiza saldo localmente
            account.setBalance(originalBalance + request.getAmount());
            accountRepository.save(account);

            // 2️⃣ Registra transacción en otro microservicio
            TransactionResponseDTO transaction = transactionsClient.createTransaction(accountId, request);

            log.info("✅ Depósito exitoso en cuenta {}. Nuevo saldo: {}", accountId, account.getBalance());
            return transaction;

        } catch (Exception ex) {
            log.error("❌ Error al procesar depósito, rollback activado. Balance restaurado a {}", originalBalance, ex);

            throw new RuntimeException("No se pudo registrar el ingreso. Operación revertida.", ex);
        }
    }

    // =============================================================
    //  Obtener transacciones (transactions-service)
    // =============================================================
    public List<TransactionResponseDTO> getAccountTransactions(String accountId) {
        return transactionsClient.getAllTransactions(accountId);
    }

    public List<TransactionResponseDTO> getLast5Transactions(String accountId) {
        return transactionsClient.getLast5Transactions(accountId);
    }

    public TransactionResponseDTO getTransactionByIdAndAccountId(String accountId, String transactionId) {
        return transactionsClient.getTransactionByIdAndAccountId(accountId, transactionId);
    }

    // =============================================================
    //  Helpers
    // =============================================================
    public void deleteAccount(String id) {
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Cuenta no encontrada");
        }
        accountRepository.deleteById(id);
    }

    private String extractErrorMessage(String json) {
        try {
            Map<String, String> map = new ObjectMapper().readValue(json, Map.class);
            return map.getOrDefault("error", "Error desconocido");
        } catch (Exception ex) {
            return "Error al procesar la respuesta";
        }
    }

    private String generateCvu() {
        Random random = new Random();
        StringBuilder cvu = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            cvu.append(random.nextInt(10));
        }
        return cvu.toString();
    }

    private String generateAlias() {
        if (wordList.isEmpty()) return "alias.default.error";
        Random random = new Random();
        return String.format("%s.%s.%s",
                wordList.get(random.nextInt(wordList.size())),
                wordList.get(random.nextInt(wordList.size())),
                wordList.get(random.nextInt(wordList.size()))).toLowerCase();
    }

    private List<String> loadWords() {
        List<String> words = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream("words.txt"))))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) words.add(line.trim());
            }
        } catch (IOException | NullPointerException e) {
            log.error("Error al cargar words.txt", e);
        }
        return words;
    }

    private AccountResponseDTO toResponseDTO(Account account) {
        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(account.getId());
        dto.setUserId(account.getUserId());
        dto.setCvu(account.getCvu());
        dto.setAlias(account.getAlias());
        dto.setBalance(account.getBalance());
        dto.setCurrency(account.getCurrency());
        return dto;
    }
}
