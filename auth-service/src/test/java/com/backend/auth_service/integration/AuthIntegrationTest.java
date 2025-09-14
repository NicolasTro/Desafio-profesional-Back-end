package com.backend.auth_service.integration;

import com.backend.auth_service.model.dto.LoginRequest;
import com.backend.auth_service.model.domain.User;
import com.backend.auth_service.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@AutoConfigureMockMvc // 👈 agrega esta
class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();

        User user = new User();
        user.setEmail("juan@example.com");
        user.setPassword(new BCryptPasswordEncoder().encode("123456")); // 👉 encode con BCrypt
        userRepository.save(user);
    }
    @Autowired
    private Environment environment;

    @Test
    void contextLoads() {
        System.out.println("Active profiles: " + Arrays.toString(environment.getActiveProfiles()));
        System.out.println("Datasource URL: " + environment.getProperty("spring.datasource.url"));
    }

    @Test
    void login_shouldReturn200_whenCredentialsAreValid() throws Exception {
        String body = """
            {
              "email": "juan@example.com",
              "password": "123456"
            }
            """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk());
    }

    @Test
    void login_shouldReturn401_whenCredentialsAreInvalid() throws Exception {
        String body = """
            {
              "email": "juan@example.com",
              "password": "wrongpass"
            }
            """;

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isUnauthorized());
    }
}
