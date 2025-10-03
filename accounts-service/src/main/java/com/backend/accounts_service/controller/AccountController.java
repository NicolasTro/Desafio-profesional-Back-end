package com.backend.accounts_service.controller;

import com.backend.accounts_service.model.dto.*;
import com.backend.accounts_service.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
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

    /**
     * Obtener los últimos 5 movimientos de la cuenta
     */
    @GetMapping("/{cvu}/transactions")
    public ResponseEntity<List<TransactionSummaryDTO>> getLast5Transactions(@PathVariable String cvu) {
        return ResponseEntity.ok(accountService.getLast5Transactions(cvu));
    }

    /**
     * Obtener todas las tarjetas asociadas a la cuenta
     */
    @GetMapping("/{cvu}/cards")
    public ResponseEntity<List<CardSummaryDTO>> getCards(@PathVariable String cvu) {
        return ResponseEntity.ok(accountService.getCards(cvu));
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



}
