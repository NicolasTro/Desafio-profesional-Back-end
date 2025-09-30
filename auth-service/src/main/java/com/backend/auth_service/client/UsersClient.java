package com.backend.auth_service.client;

import com.backend.auth_service.model.dto.UserProfileRequest;
import com.backend.auth_service.model.dto.RegisterResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "users-service", configuration = com.backend.auth_service.config.FeignConfig.class)
public interface UsersClient {
    @PostMapping("/users")
    RegisterResponse createUserProfile(@RequestBody UserProfileRequest profile);
}
