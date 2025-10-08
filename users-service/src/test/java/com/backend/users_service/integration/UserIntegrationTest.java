package com.backend.users_service.integration;

import com.backend.users_service.model.domain.User;
import com.backend.users_service.model.dto.UserProfileRequest;
import com.backend.users_service.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
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
@org.springframework.test.context.ActiveProfiles("test")
@org.springframework.context.annotation.Import(com.backend.users_service.config.TestConfig.class)
@AutoConfigureMockMvc
class UserIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @org.springframework.boot.test.mock.mockito.MockBean
    private com.backend.users_service.filter.InternalKeyFilter internalKeyFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @org.junit.jupiter.api.BeforeEach
    void setUpFilterMock() throws Exception {
        // Ensure the mocked filter continues the chain so the request reaches controllers
        try {
            org.mockito.Mockito.doAnswer(invocation -> {
                jakarta.servlet.ServletRequest req = invocation.getArgument(0);
                jakarta.servlet.ServletResponse res = invocation.getArgument(1);
                jakarta.servlet.FilterChain chain = invocation.getArgument(2);
                try {
                    chain.doFilter(req, res);
                } catch (jakarta.servlet.ServletException | java.io.IOException e) {
                    throw new RuntimeException(e);
                }
                return null;
            }).when(internalKeyFilter).doFilter(org.mockito.Mockito.any(), org.mockito.Mockito.any(), org.mockito.Mockito.any());
        } catch (jakarta.servlet.ServletException | java.io.IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void registerUser_shouldPersistInDatabase() throws Exception {
    UserProfileRequest request = new UserProfileRequest();
        // In the real flow auth-service provides a userId; emulate that here
        request.setUserId(UUID.randomUUID().toString());
        request.setNombre("Juan");
        request.setApellido("Pérez");
        request.setDni("12345678");
        request.setEmail("juan@example.com");
        request.setTelefono("099123456");
    mockMvc.perform(post("/users")
            .header("X-Internal-Key", "dmh-internal-key-please-change")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@example.com"));

        // validar que se guardó en DB
        User saved = userRepository.findByEmail("juan@example.com").orElse(null);
        assertThat(saved).isNotNull();
        assertThat(saved.getUserId()).isNotNull();
        assertThat(saved.getEmail()).isEqualTo("juan@example.com");
    }
}
