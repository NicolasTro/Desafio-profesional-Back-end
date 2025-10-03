package com.backend.accounts_service.service;

import com.backend.accounts_service.client.TransactionsClient;
import com.backend.accounts_service.client.CardsClient;
import com.backend.accounts_service.model.domain.Account;
import com.backend.accounts_service.model.dto.*;
import com.backend.accounts_service.repository.AccountRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionsClient transactionsClient;
    private final CardsClient cardsClient;

    public AccountService(AccountRepository accountRepository,
                          TransactionsClient transactionsClient,
                          CardsClient cardsClient) {
        this.accountRepository = accountRepository;
        this.transactionsClient = transactionsClient;
        this.cardsClient = cardsClient;
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

        Account account = Account.builder()
                .userId(dto.getUserId())             // 👈 usar el mismo userId del Saga
                .cvu(generateCvu())
                .alias(generateAlias(dto.getUserId()))
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
        Account account = accountRepository.findByCvu(cvu)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        return toResponseDTO(account);
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
     * Obtener últimos 5 movimientos de la cuenta (via transactions-service)
     */
    public List<TransactionSummaryDTO> getLast5Transactions(String cvu) {
        accountRepository.findByCvu(cvu)
                .orElseThrow(() -> new RuntimeException("Cuenta no encontrada"));
        return transactionsClient.getLast5Transactions(cvu);
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

    private String generateAlias(String userId) {
        return "alias." + userId.substring(0, 5).toLowerCase();
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



}
