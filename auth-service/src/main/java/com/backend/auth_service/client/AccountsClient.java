package com.backend.auth_service.client;

import com.backend.auth_service.config.FeignConfig;
import com.backend.auth_service.model.dto.AccountCreateDTO;
import com.backend.auth_service.model.dto.AccountResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "accounts-service", configuration = FeignConfig.class)
public interface AccountsClient {
    @PostMapping("/accounts")
    AccountResponseDTO createAccount(@RequestBody AccountCreateDTO request);

    @DeleteMapping("/accounts/{id}")
    void deleteAccount(@PathVariable String id);
}