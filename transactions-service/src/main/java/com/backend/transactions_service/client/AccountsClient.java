package com.backend.transactions_service.client;

import com.backend.transactions_service.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

//@FeignClient(name = "accounts-service", configuration = FeignConfig.class)
@FeignClient(name = "ACCOUNTS-SERVICE")
public interface AccountsClient {

    @PatchMapping("/{accountId}/balance")
    void updateBalance(
            @PathVariable("accountId") String accountId,
            @RequestParam("amount") Double amount,
            @RequestParam("type") String type
    );
}
