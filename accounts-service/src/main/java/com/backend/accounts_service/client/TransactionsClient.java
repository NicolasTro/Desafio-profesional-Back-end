package com.backend.accounts_service.client;

import com.backend.accounts_service.model.dto.TransactionResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

//@FeignClient(name = "transactions-service", path = "/transactions")

@FeignClient(name = "TRANSACTIONS-SERVICE")
public interface TransactionsClient {

    /**
     * Obtener los últimos 5 movimientos de una cuenta por CVU
     */
    @GetMapping("/account/{cvu}/last5")
    List<TransactionResponseDTO> getLast5Transactions(@PathVariable("cvu") String cvu);

    /**
     * Obtener todas las transacciones de una cuenta por CVU
     */
    @GetMapping("/account/{cvu}")
    List<TransactionResponseDTO> getAllTransactions(@PathVariable("cvu") String cvu);
}


