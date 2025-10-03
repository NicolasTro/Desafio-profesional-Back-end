package com.backend.users_service.service;

import com.backend.users_service.model.domain.User;
import com.backend.users_service.model.dto.UserProfileRequest;
import com.backend.users_service.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Service
@Log4j2
public class UserService {

    private final UserRepository userRepository;
    private final Random random = new SecureRandom();
    private final List<String> words;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.words = loadWordsFromFile();
    }

    public String registerUser(UserProfileRequest request) {
        User user = User.builder()
                .userId(request.getUserId())            // 👈 usar el mismo userId del Saga
                .nombre(request.getNombre())
                .apellido(request.getApellido())
                .dni(request.getDni())
                .email(request.getEmail())
                .telefono(request.getTelefono())
                .build();

        userRepository.save(user);
        return request.getUserId(); // devolver el mismo userId
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado");
        }
        userRepository.deleteById(id);
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

