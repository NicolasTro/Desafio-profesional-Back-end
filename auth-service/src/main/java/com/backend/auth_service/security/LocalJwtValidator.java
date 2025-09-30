package com.backend.auth_service.security;

import com.backend.auth_service.util.JwtUtil;
import org.springframework.stereotype.Component;

@Component
public class LocalJwtValidator implements TokenValidator {

    private final JwtUtil jwtUtil;

    public LocalJwtValidator(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public String getSubject(String token) {
        return jwtUtil.getSubjectFromToken(token);
    }

    @Override
    public boolean isValid(String token) {
        try {
            jwtUtil.getSubjectFromToken(token); // si no explota, es válido
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
