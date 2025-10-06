package com.backend.users_service.client;

import com.backend.users_service.model.dto.AccountResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ACCOUNTS-SERVICE")
public interface AccountsClient {

    @GetMapping("/accounts/user/{userId}")
    AccountResponseDTO getAccountByUserId(@PathVariable("userId") String userId);
}
