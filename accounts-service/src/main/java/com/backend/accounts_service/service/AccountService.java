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
        wordList = loadWords();
    }

    /**
     * Crear cuenta para un usuario (usado al registrar en auth-service)
     */
    public AccountResponseDTO createAccount(AccountCreateDTO dto) {
        // validar que no exista ya una cuenta para este usuario
        Optional<Account> existing = accountRepository.findByUserId(dto.getUserId());
        if (existing.isPresent()) {
            throw new RuntimeException("El usuario ya tiene una cuenta asociada");
        }
        log.info("llego a account");


        Account account = Account.builder()
                .userId(dto.getUserId())             // 👈 usar el mismo userId del Saga
                .cvu(generateCvu())
                .alias(generateAlias())
                .balance(0.0)
                .currency("ARS")
                .build();

        Account saved = accountRepository.save(account);
        return toResponseDTO(saved);
    }

    /**
     * Obtener cuenta por CVU
     */
    public AccountResponseDTO getAccountByCvu(String cvu) {
        Optional<Account> account = accountRepository.findByCvu(cvu);
        if (account.isEmpty()) {
            throw new ResourceNotFoundException("Cuenta no encontrada");
        }

        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(account.get().getId());
        dto.setUserId(account.get().getUserId());
        dto.setCvu(account.get().getCvu());
        dto.setAlias(account.get().getAlias());
        dto.setBalance(account.get().getBalance());
        dto.setCurrency(account.get().getCurrency());

        return dto;
    }


    /**
     * Obtener cuenta por CVU
     */
    public AccountResponseDTO getAccountByUserId(String userId) {
        Optional<Account> account = accountRepository.findByUserId(userId);
        if (account.isEmpty()) {
            throw new ResourceNotFoundException("Cuenta no encontrada");
        }

        AccountResponseDTO dto = new AccountResponseDTO();
        dto.setId(account.get().getId());
        dto.setUserId(account.get().getUserId());
        dto.setCvu(account.get().getCvu());
        dto.setAlias(account.get().getAlias());
        dto.setBalance(account.get().getBalance());
        dto.setCurrency(account.get().getCurrency());

        return dto;
    }



    /**
     * Actualizar alias o currency de la cuenta
     */
    public AccountResponseDTO updateAccount(String cvu, AccountUpdateDTO dto) {
        Account account = accountRepository.findByCvu(cvu)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));

        if (dto.getAlias() != null) account.setAlias(dto.getAlias());
        if (dto.getCurrency() != null) account.setCurrency(dto.getCurrency());

        return toResponseDTO(accountRepository.save(account));
    }


    /**
     * Obtener tarjetas asociadas a la cuenta (via cards-service)
     */
    public List<CardSummaryDTO> getCards(String cvu) {
        accountRepository.findByCvu(cvu)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        return cardsClient.getCardsByAccount(cvu);
    }


    /**
     * Agregar una tarjeta nueva asociada a un CVU.
     */
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


    /**
     * Obtener una tarjeta específica por CVU + cardId
     */
    public CardResponseDTO getCardById(String cvu, String cardId) {
        try {

            return cardsClient.getCardById(cvu, cardId);

        } catch (FeignException e) {
            String errorMessage = extractErrorMessage(e.contentUTF8());
            throw new ResponseStatusException(
                    HttpStatus.valueOf(e.status()), errorMessage);
        }
    }

    /** Extraer mensaje de error de la respuesta JSON
     */

    private String extractErrorMessage(String json) {
        try {
            Map<String, String> map = new ObjectMapper().readValue(json, Map.class);
            return map.getOrDefault("error", "Error desconocido");
        } catch (Exception ex) {
            return "Error al procesar la respuesta";
        }
    }


    /**
     * Eliminar una tarjeta específica por CVU + cardId
     */
    public void deleteCard(String cvu, String cardId) {
        cardsClient.deleteCard(cvu, cardId);
    }


    /**
     * Actualizar balance de la cuenta (usado desde transactions-service)
     */
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

    // ---------- Helpers ----------

    private String generateCvu() {
        Random random = new Random();
        StringBuilder cvu = new StringBuilder();
        for (int i = 0; i < 22; i++) {
            cvu.append(random.nextInt(10));
        }
        return cvu.toString();
    }

    private String generateAlias() {
        if (wordList.isEmpty()) {
            return "alias.default.error";
        }
        Random random = new Random();
        String w1 = wordList.get(random.nextInt(wordList.size()));
        String w2 = wordList.get(random.nextInt(wordList.size()));
        String w3 = wordList.get(random.nextInt(wordList.size()));
        return String.format("%s.%s.%s", w1, w2, w3).toLowerCase();
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
            log.error("Error al cargar word.txt", e);
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


    public void deleteAccount(String id) {
        if (!accountRepository.existsById(id)) {
            throw new RuntimeException("Cuenta no encontrada");
        }
        accountRepository.deleteById(id);
    }


    /**
     * Obtener todas las transacciones de una cuenta
     */
    public List<TransactionResponseDTO> getAccountTransactions(String accountId) {
        return transactionsClient.getAllTransactions(accountId);
    }

    /**
     * Obtener los últimos 5 movimientos
     */
    public List<TransactionResponseDTO> getLast5Transactions(String accountId) {
        return transactionsClient.getLast5Transactions(accountId);
    }


}
