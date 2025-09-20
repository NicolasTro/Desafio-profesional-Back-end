package com.backend.users_service.service;

import com.backend.users_service.exception.ValidationException;
import com.backend.users_service.model.domain.User;
//import com.backend.users_service.model.dto.UserRegisterRequest;
import com.backend.users_service.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    private UserRegisterRequest buildValidRequest() {
//        UserRegisterRequest request = new UserRegisterRequest();
//        request.setNombre("Juan");
//        request.setApellido("Pérez");
//        request.setDni("12345678");
//        request.setEmail("juan@example.com");
//        request.setTelefono("099123456");
//        request.setPassword("123456");
//        return request;
//    }
//
//    @Test
//    void registerUser_shouldEncryptPasswordAndSave() {
//        UserRegisterRequest request = buildValidRequest();
//
//        when(userRepository.findByEmail("juan@example.com")).thenReturn(Optional.empty());
//        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArguments()[0]);
//
//        User savedUser = userService.registerUser(request);
//
//        assertNotNull(savedUser);
//        assertNotEquals("123456", savedUser.getPassword());
//        assertTrue(savedUser.getPassword().startsWith("$2a$"));
//
//        assertNotNull(savedUser.getCvu());
//        assertEquals(22, savedUser.getCvu().length());
//        assertNotNull(savedUser.getAlias());
//        assertEquals(3, savedUser.getAlias().split("\\.").length);
//
//        verify(userRepository, times(1)).save(any(User.class));
//    }
//
//    @Test
//    void registerUser_withNullPassword_shouldThrowValidationException() {
//        UserRegisterRequest request = buildValidRequest();
//        request.setPassword(null); // 👈 password nula
//
//        assertThrows(ValidationException.class, () -> userService.registerUser(request));
//
//        verify(userRepository, never()).save(any(User.class));
//        verify(passwordEncoder, never()).encode(anyString());
//    }
//
//    @Test
//    void registerUser_withEmptyPassword_shouldThrowValidationException() {
//        UserRegisterRequest request = buildValidRequest();
//        request.setPassword(""); // 👈 password vacía
//
//        assertThrows(ValidationException.class, () -> userService.registerUser(request));
//
//        verify(userRepository, never()).save(any(User.class));
//        verify(passwordEncoder, never()).encode(anyString());
//    }
//
//    @Test
//    void registerUser_withNullDni_shouldThrowValidationException() {
//        UserRegisterRequest request = buildValidRequest();
//        request.setDni(null);
//
//        assertThrows(ValidationException.class, () -> userService.registerUser(request));
//
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void registerUser_withEmptyDni_shouldThrowValidationException() {
//        UserRegisterRequest request = buildValidRequest();
//        request.setDni("");
//
//        assertThrows(ValidationException.class, () -> userService.registerUser(request));
//
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void registerUser_withNullTelefono_shouldThrowValidationException() {
//        UserRegisterRequest request = buildValidRequest();
//        request.setTelefono(null);
//
//        assertThrows(ValidationException.class, () -> userService.registerUser(request));
//
//        verify(userRepository, never()).save(any(User.class));
//    }
//
//    @Test
//    void registerUser_withEmptyTelefono_shouldThrowValidationException() {
//        UserRegisterRequest request = buildValidRequest();
//        request.setTelefono("");
//
//        assertThrows(ValidationException.class, () -> userService.registerUser(request));
//
//        verify(userRepository, never()).save(any(User.class));
//    }


}
