package com.backend.users_service.security;

public interface TokenValidator {
    String getSubject(String token);
    boolean isValid(String token);
}
