package com.backend.users_service.integration;

import com.backend.users_service.model.domain.User;
import com.backend.users_service.model.dto.UserRegisterRequest;
import com.backend.users_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void registerUser_shouldPersistInDatabase() throws Exception {
        UserRegisterRequest request = new UserRegisterRequest();
        request.setNombre("Juan");
        request.setApellido("Pérez");
        request.setDni("12345678");
        request.setEmail("juan@example.com");
        request.setTelefono("099123456");
        request.setPassword("123456");

        mockMvc.perform(post("/users/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@example.com"));

        // validar que se guardó en DB
        User saved = userRepository.findByEmail("juan@example.com").orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getPassword()).isNotEqualTo("123456"); // debe estar encriptada
        assertThat(saved.getCvu()).hasSize(22);
        assertThat(saved.getAlias().split("\\.")).hasSize(3);
    }
}
