package com.backend.users_service.controller;

import com.backend.users_service.model.domain.User;
import com.backend.users_service.model.dto.UserProfileRequest;
import com.backend.users_service.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

//    @PostMapping(value = {"","/register"})
@PostMapping
public ResponseEntity<String> createUser(@RequestBody UserProfileRequest request) {
    String userId = userService.registerUser(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(userId);
}

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable String id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

}
