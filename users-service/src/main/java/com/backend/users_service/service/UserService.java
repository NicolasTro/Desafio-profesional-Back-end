package com.backend.users_service.service;

import com.backend.users_service.model.domain.User;
import com.backend.users_service.model.dto.UserProfileRequest;
import com.backend.users_service.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final Random random = new SecureRandom();
    private final List<String> words;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.words = loadWordsFromFile();
    }

    public User registerUser(UserProfileRequest request) {
        // Validar email único
        userRepository.findByEmail(request.getEmail())
                .ifPresent(u -> { throw new RuntimeException("El email ya está registrado"); });

        // Generar CVU
        String cvu = generateCvu();

        // Generar alias
        String alias = generateAlias();

        User user = User.builder()
                .id(request.getUserId())
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .dni(request.getDni())
                .email(request.getEmail())
                .telefono(request.getTelefono())
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
            return reader.lines().map(String::trim).filter(s -> !s.isEmpty()).toList();
        } catch (Exception e) {
            throw new RuntimeException("Error cargando words.txt", e);
        }
    }
}

