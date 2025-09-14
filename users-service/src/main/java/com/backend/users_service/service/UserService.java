package com.backend.users_service.service;

import com.backend.users_service.exception.ResourceNotFoundException;
import com.backend.users_service.exception.ValidationException;
import com.backend.users_service.model.dto.UserRegisterRequest;
import com.backend.users_service.model.domain.User;
import com.backend.users_service.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.*;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final Random random = new SecureRandom();
    private final List<String> words;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
        this.words = loadWordsFromFile();
    }

    public User registerUser(UserRegisterRequest request) {
        // ✅ Validar email nulo/vacío
        if (request.getEmail() == null || request.getEmail().isBlank()) {
            throw new ValidationException("El email no puede ser nulo o vacío");
        }

        // ✅ Validar password nulo/vacío y longitud
        if (request.getPassword() == null || request.getPassword().isBlank()) {
            throw new ValidationException("La contraseña no puede ser nula o vacía");
        }
        if (request.getPassword().length() < 6) {
            throw new ValidationException("La contraseña debe tener al menos 6 caracteres");
        }

        // ✅ Validar DNI nulo/vacío
        if (request.getDni() == null || request.getDni().isBlank()) {
            throw new ValidationException("El DNI no puede ser nulo o vacío");
        }

        // ✅ Validar teléfono nulo/vacío
        if (request.getTelefono() == null || request.getTelefono().isBlank()) {
            throw new ValidationException("El teléfono no puede ser nulo o vacío");
        }

        // ✅ Validar email único
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> {
                    throw new ValidationException("El email ya está registrado");
                });

        // Generar CVU (22 dígitos)
        String cvu = generateCvu();

        // Generar alias (desde words.txt)
        String alias = generateAlias();

        User user = User.builder()
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .dni(request.getDni())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .password(passwordEncoder.encode(request.getPassword()))
                .cvu(cvu)
                .alias(alias)
                .build();

        return userRepository.save(user);
    }

    private String generateCvu() {
        StringBuilder sb = new StringBuilder(22);
        for (int i = 0; i < 22; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateAlias() {
        if (words.isEmpty()) {
            throw new RuntimeException("No hay palabras disponibles en words.txt");
        }
        return words.get(random.nextInt(words.size())) + "." +
                words.get(random.nextInt(words.size())) + "." +
                words.get(random.nextInt(words.size()));
    }

    private List<String> loadWordsFromFile() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(Objects.requireNonNull(
                        getClass().getClassLoader().getResourceAsStream("words.txt"))))) {
            return reader.lines().toList();
        } catch (Exception e) {
            throw new RuntimeException("Error cargando words.txt", e);
        }
    }

    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario con id " + id + " no existe"));
    }
}
