package com.backend.auth_service.service;

import com.backend.auth_service.model.dto.LoginRequest;
import com.backend.auth_service.model.domain.User;
import com.backend.auth_service.repository.UserRepository;
import com.backend.auth_service.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

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

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encoded-pass");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("123456", "encoded-pass"))
                .thenReturn(true);
        when(jwtUtil.generateToken("test@example.com"))
                .thenReturn("fake-jwt");

        // when
        String token = authService.login(request);

        // then
        assertNotNull(token);
        assertEquals("fake-jwt", token);
    }

    @Test
    void login_shouldThrowException_whenUserNotFound() {
        LoginRequest request = new LoginRequest();
        request.setEmail("missing@example.com");
        request.setPassword("123456");

        when(userRepository.findByEmail("missing@example.com"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }

    @Test
    void login_shouldThrowException_whenPasswordIsInvalid() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@example.com");
        request.setPassword("wrongpass");

        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword("encoded-pass");

        when(userRepository.findByEmail("test@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrongpass", "encoded-pass"))
                .thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(request));
    }
}
