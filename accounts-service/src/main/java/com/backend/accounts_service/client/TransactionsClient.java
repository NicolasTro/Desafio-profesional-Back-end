package com.backend.accounts_service.client;

import com.backend.accounts_service.config.FeignConfig;
import com.backend.accounts_service.model.dto.TransactionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;



@FeignClient(
        name = "TRANSACTIONS-SERVICE",
        configuration = FeignConfig.class
)
public interface TransactionsClient {

    /**
     * Obtener los últimos 5 movimientos de una cuenta por CVU
     */
    @GetMapping("/transactions/{accountId}/last5")
    List<TransactionResponseDTO> getLast5Transactions(@PathVariable("accountId") String accountId);

    /**
     * Obtener todas las transacciones de una cuenta por CVU
     */
    @GetMapping("/transactions/{accountId}/activity")
    List<TransactionResponseDTO> getAllTransactions(@PathVariable("accountId") String accountId);




}


