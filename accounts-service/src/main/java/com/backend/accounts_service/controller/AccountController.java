package com.backend.accounts_service.controller;

import com.backend.accounts_service.model.dto.*;
import com.backend.accounts_service.service.AccountService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Crear cuenta (usado por auth-service al registrar un usuario)
     */
    @PostMapping
    public ResponseEntity<AccountResponseDTO> createAccount(@RequestBody AccountCreateDTO dto) {
        log.info("esta llegando al controler post");
        AccountResponseDTO response = accountService.createAccount(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }



    /**
     * Obtener información de una cuenta por CVU
     */
    @GetMapping("/{cvu}")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable String cvu) {
        return ResponseEntity.ok(accountService.getAccountByCvu(cvu));
    }

    /**
     * Actualizar datos de la cuenta (alias, currency)
     */
    @PatchMapping("/{cvu}")
    public ResponseEntity<AccountResponseDTO> updateAccount(
            @PathVariable String cvu,
            @RequestBody AccountUpdateDTO request) {
        return ResponseEntity.ok(accountService.updateAccount(cvu, request));
    }

//    /**
//     * Obtener los últimos 5 movimientos de la cuenta
//     */
//    @GetMapping("/{cvu}/transactions")
//    public ResponseEntity<List<TransactionResponseDTO>> getLast5Transactions(@PathVariable String cvu) {
//        return ResponseEntity.ok(accountService.getLast5Transactions(cvu));
//    }

    /**
     * Obtener todas las tarjetas asociadas a la cuenta
     */
    @GetMapping("/{cvu}/cards")
    public ResponseEntity<List<CardSummaryDTO>> getCards(@PathVariable String cvu) {
        return ResponseEntity.ok(accountService.getCards(cvu));
    }

    /**
     * Agregar una nueva tarjeta asociada a un CVU.
     */
    @PostMapping("/{cvu}/cards")
    public ResponseEntity<CardResponseDTO> addCard(
            @PathVariable String cvu,
            @RequestBody CardRequestDTO request) {

        CardResponseDTO response = accountService.addCard(cvu, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    /**
     * Obtener una tarjeta específica por CVU + cardId
     */
    @GetMapping("/{cvu}/cards/{cardId}")
    public ResponseEntity<CardResponseDTO> getCardById(
            @PathVariable String cvu,
            @PathVariable String cardId) {
        return ResponseEntity.ok(accountService.getCardById(cvu, cardId));
    }


    /**
     * Eliminar una tarjeta específica por CVU + cardId
     */
    @DeleteMapping("/{cvu}/cards/{cardId}")
    public ResponseEntity<Void> deleteCard(
            @PathVariable String cvu,
            @PathVariable String cardId) {
        accountService.deleteCard(cvu, cardId);
        return ResponseEntity.noContent().build(); // 204 No Content
    }


    /**
     * Actualizar balance de la cuenta (se usa desde transactions-service)
     */
    @PatchMapping("/{cvu}/balance")
    public ResponseEntity<Void> updateBalance(
            @PathVariable String cvu,
            @RequestParam Double amount,
            @RequestParam String type) {
        accountService.updateBalance(cvu, amount, type);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable String id) {
        accountService.deleteAccount(id);
        return ResponseEntity.noContent().build();
    }
//    /**
//     * Obtener las transacciones de una cuenta
//     */
//    @GetMapping("/{accountId}/transactions")
//    public ResponseEntity<List<TransactionResponseDTO>> getAccountTransactions(
//            @PathVariable String accountId) {
//
//        List<TransactionResponseDTO> transactions = accountService.getAccountTransactions(accountId);
//        return ResponseEntity.ok(transactions);
//    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<AccountResponseDTO> getAccountsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(accountService.getAccountByUserId(userId));
    }


    @GetMapping("/{cvu}/transactions")
    public ResponseEntity<List<TransactionResponseDTO>> getAllTransactions(@PathVariable String cvu) {
        return ResponseEntity.ok(accountService.getAccountTransactions(cvu));
    }

    @GetMapping("/{cvu}/transactions/last5")
    public ResponseEntity<List<TransactionResponseDTO>> getLast5Transactions(@PathVariable String cvu) {
        return ResponseEntity.ok(accountService.getLast5Transactions(cvu));
    }



}
