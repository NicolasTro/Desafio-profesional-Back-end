package com.backend.users_service.controller;

import com.backend.users_service.model.domain.User;
//import com.backend.users_service.model.dto.UserRegisterRequest;
import com.backend.users_service.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

//    @Test
//    void registerUser_shouldReturn200AndUser() throws Exception {
//        // request simulado
//        UserRegisterRequest request = new UserRegisterRequest();
//        request.setNombre("Juan");
//        request.setApellido("Pérez");
//        request.setDni("12345678");
//        request.setEmail("juan@example.com");
//        request.setTelefono("099123456");
//        request.setPassword("123456");
//
//        // respuesta mock
//        User mockUser = User.builder()
//                .id(1L)
//                .nombre("Juan")
//                .apellido("Pérez")
//                .dni("12345678")
//                .email("juan@example.com")
//                .telefono("099123456")
//                .password("hashedPassword")
//                .cvu("1234567890123456789012")
//                .alias("alias.test.user")
//                .build();
//
//        Mockito.when(userService.registerUser(any(UserRegisterRequest.class)))
//                .thenReturn(mockUser);
//
//        // ejecución del POST
//        mockMvc.perform(post("/users/register")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(request)))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.nombre").value("Juan"))
//                .andExpect(jsonPath("$.email").value("juan@example.com"));
//    }
}
