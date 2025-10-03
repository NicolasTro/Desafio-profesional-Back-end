package com.backend.accounts_service.client;

import com.backend.accounts_service.model.dto.TransactionSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "transactions-service", path = "/transactions")
public interface TransactionsClient {

    /**
     * Obtener los últimos 5 movimientos de una cuenta por CVU
     */
    @GetMapping("/accounts/{cvu}")
    List<TransactionSummaryDTO> getLast5Transactions(@PathVariable("cvu") String cvu);
}
