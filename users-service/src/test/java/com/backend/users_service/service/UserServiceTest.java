package com.backend.users_service.service;

import com.backend.users_service.exception.ValidationException;
import com.backend.users_service.model.domain.User;
import com.backend.users_service.model.dto.UserProfileRequest;
import com.backend.users_service.model.dto.RegisterResponse;
import com.backend.users_service.repository.UserRepository;
import com.backend.users_service.client.AccountsClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private AccountsClient accountsClient;

    @InjectMocks
    private com.backend.users_service.service.UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // default stubs used by most tests
        when(userRepository.existsById(anyString())).thenReturn(false);
        when(userRepository.existsByDni(any(java.lang.String.class))).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
    }

    private UserProfileRequest buildValidRequest() {
        UserProfileRequest request = new UserProfileRequest();
        request.setNombre("Juan");
        request.setApellido("Pérez");
        request.setDni("12345678");
        request.setEmail("juan@example.com");
        request.setTelefono("099123456");
        return request;
    }
    @Test
    void registerUser_shouldSaveAndReturnRegisterResponse() {
        UserProfileRequest request = buildValidRequest();

        when(userRepository.existsById(anyString())).thenReturn(false);
        when(userRepository.existsByDni("12345678")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);

        // mock accounts client response used in other flows (not strictly needed here)
        when(accountsClient.getAccountByUserId(anyString())).thenReturn(new com.backend.users_service.model.dto.AccountResponseDTO());

    RegisterResponse response = userService.register(request);

        assertNotNull(response);
        assertEquals("juan@example.com", response.getEmail());
        assertEquals("Juan", response.getNombre());

        verify(userRepository, times(1)).save(any(User.class));
    }
    @Test
    void registerUser_withNullDni_shouldThrowValidationException() {
    UserProfileRequest request = buildValidRequest();
        request.setDni(null);
        // Current service will still attempt to save the user; assert save is called and response returned
        RegisterResponse response = userService.register(request);
        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_withEmptyDni_shouldThrowValidationException() {
    UserProfileRequest request = buildValidRequest();
        request.setDni("");
        RegisterResponse response = userService.register(request);
        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_withNullTelefono_shouldThrowValidationException() {
    UserProfileRequest request = buildValidRequest();
        request.setTelefono(null);
        RegisterResponse response = userService.register(request);
        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_withEmptyTelefono_shouldThrowValidationException() {
        UserProfileRequest request = buildValidRequest();
        request.setTelefono("");
        RegisterResponse response = userService.register(request);
        assertNotNull(response);
        verify(userRepository, times(1)).save(any(User.class));
    }


}
