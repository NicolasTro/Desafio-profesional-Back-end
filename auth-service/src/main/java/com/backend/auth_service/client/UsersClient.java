package com.backend.auth_service.client;

import com.backend.auth_service.model.dto.RegisterRequest;
import com.backend.auth_service.model.dto.UserProfileRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "users-service")
public interface UsersClient {

    @PostMapping("/users")
    String createUser(@RequestBody UserProfileRequest request);

    @DeleteMapping("/users/{id}")
    void deleteUser(@PathVariable String id);
}
