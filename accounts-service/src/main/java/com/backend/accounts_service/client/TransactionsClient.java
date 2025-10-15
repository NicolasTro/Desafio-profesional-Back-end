package com.backend.accounts_service.client;

import com.backend.accounts_service.config.FeignConfig;
import com.backend.accounts_service.model.dto.TransactionRequestDTO;
import com.backend.accounts_service.model.dto.TransactionResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@FeignClient(
        name = "TRANSACTIONS-SERVICE",
        configuration = FeignConfig.class
)
public interface TransactionsClient {

    // =========================================================
    // 💳 Registrar ingreso de dinero (depósito)
    // =========================================================
    @PostMapping("/transactions/{accountId}/transferences")
    @CircuitBreaker(name = "transactionsService", fallbackMethod = "fallbackTransaction")
    @Retry(name = "transactionsRetry")
    TransactionResponseDTO createTransaction(
            @PathVariable("accountId") String accountId,
            @RequestBody TransactionRequestDTO request
    );

    // =========================================================
    // 🔁 Registrar transferencia entre cuentas
    // =========================================================
    @PostMapping("/transactions/transfer")
    @CircuitBreaker(name = "transactionsService", fallbackMethod = "fallbackTransactionGeneric")
    @Retry(name = "transactionsRetry")
    TransactionResponseDTO transfer(@RequestBody TransactionRequestDTO request);

    // =========================================================
    // 📋 Obtener las últimas 5 transacciones
    // =========================================================
    @GetMapping("/transactions/{accountId}/last5")
    List<TransactionResponseDTO> getLast5Transactions(@PathVariable("accountId") String accountId);

    // =========================================================
    // 📜 Obtener todas las transacciones (actividad completa)
    // =========================================================
    @GetMapping("/transactions/{accountId}/activity")
    List<TransactionResponseDTO> getAllTransactions(@PathVariable("accountId") String accountId);

    // =========================================================
    // 🔍 Obtener una transacción específica por ID
    // =========================================================
    @GetMapping("/transactions/{accountId}/activity/{transferenceId}")
    TransactionResponseDTO getTransactionByIdAndAccountId(
            @PathVariable("accountId") String accountId,
            @PathVariable("transferenceId") String transferenceId
    );

    // =========================================================
    // ⚙️ Métodos de fallback
    // =========================================================

    // ❌ Fallo crítico al registrar depósito o transferencia → rollback en AccountService
    default TransactionResponseDTO fallbackTransaction(String accountId,
                                                       TransactionRequestDTO request,
                                                       Throwable ex) {
        throw new RuntimeException("💥 transactions-service no disponible. Operación revertida.", ex);
    }

    default TransactionResponseDTO fallbackTransactionGeneric(TransactionRequestDTO request, Throwable ex) {
        throw new RuntimeException("💥 transactions-service no disponible. Operación revertida.", ex);
    }

    // ✅ Fallbacks de lectura → no lanzan error, devuelven datos vacíos
    default List<TransactionResponseDTO> fallbackGetAllTransactions(String accountId, Throwable ex) {
        return Collections.emptyList();
    }

    default List<TransactionResponseDTO> fallbackGetLast5Transactions(String accountId, Throwable ex) {
        return Collections.emptyList();
    }

    default TransactionResponseDTO fallbackGetTransactionByIdAndAccountId(String accountId, String transferenceId, Throwable ex) {
        TransactionResponseDTO response = new TransactionResponseDTO();
        response.setAccountId(accountId);
        response.setDescription("⚠️ No se pudo obtener la transacción solicitada. Intente más tarde.");
        return response;
    }
}
