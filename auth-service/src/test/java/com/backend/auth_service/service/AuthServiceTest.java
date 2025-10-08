package com.backend.auth_service.service;

import com.backend.auth_service.model.dto.LoginRequest;
import com.backend.auth_service.model.domain.UserCredentials;
import com.backend.auth_service.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.backend.auth_service.repository.UserCredentialsRepository;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Map;
import java.util.HashMap;

import com.backend.auth_service.model.dto.LoginResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

        @Mock
        private UserCredentialsRepository credentialsRepository;

        @Mock
        private JwtUtil jwtUtil;

        @Mock
        private PasswordEncoder passwordEncoder;

        @InjectMocks
        private AuthService authService;

    @Test
    void login_shouldReturnJwt_whenCredentialsAreValid() {
        // given
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("123456");

        UserCredentials user = new UserCredentials();
        user.setEmail("test@example.com");
        user.setPassword("encoded-pass");
        user.setUserId("user-123");

        when(credentialsRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "encoded-pass"))
                .thenReturn(true);
        when(jwtUtil.generateToken(eq("user-123"), anyMap()))
                .thenReturn("fake.jwt.token");

        // when
        LoginResponse response = authService.login(request);

        // then
        assertNotNull(response);
        assertEquals("fake.jwt.token", response.getToken());
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@example.com");
        request.setPassword("123456");

        when(credentialsRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldThrowException_whenPasswordIsInvalid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpass");

        UserCredentials user = new UserCredentials();
        user.setEmail("test@example.com");
        user.setPassword("encoded-pass");

        when(credentialsRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encoded-pass"))
                .thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }
}
