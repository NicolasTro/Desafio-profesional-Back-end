//package com.backend.auth_service.controller;
//
//
//import com.backend.auth_service.model.dto.LoginRequest;
//import com.backend.auth_service.service.AuthService;
//import com.backend.auth_service.util.JwtUtil;
//import io.jsonwebtoken.MalformedJwtException;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//
//@WebMvcTest(AuthController.class)
//@AutoConfigureMockMvc(addFilters = false)
//class AuthControllerTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private AuthService authService;
//
//    @MockBean
//    private JwtUtil jwtUtil;
//
//    @Test
//    void login_validRequest_returnsToken() throws Exception {
//        when(authService.login(any(LoginRequest.class))).thenReturn("fake.jwt.token");
//
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"email\":\"test@test.com\",\"password\":\"123456\"}"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.token").value("fake.jwt.token"));
//    }
//
//    @Test
//    void login_invalidRequest_returnsUnauthorized() throws Exception {
//        when(authService.login(any(LoginRequest.class)))
//                .thenThrow(new RuntimeException("Credenciales inválidas"));
//
//        mockMvc.perform(post("/auth/login")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{\"email\":\"wrong@test.com\",\"password\":\"badpass\"}"))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.error").value("Credenciales inválidas"));
//    }
//
//    @Test
//    void logout_withBearerToken_returnsOk() throws Exception {
//        when(jwtUtil.getSubjectFromToken("valid.jwt.token")).thenReturn("test@test.com");
//
//        mockMvc.perform(post("/auth/logout")
//                        .header("Authorization", "valid.jwt.token"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.message").value("Logout exitoso para test@test.com"));
//    }
//
//    @Test
//    void logout_withInvalidToken_returnsUnauthorized() throws Exception {
//        when(jwtUtil.getSubjectFromToken("invalid.token"))
//                .thenThrow(new MalformedJwtException("bad token"));
//
//        mockMvc.perform(post("/auth/logout")
//                        .header("Authorization", "invalid.token"))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.error").value("Token inválido"));
//    }
//
//    @Test
//    void logout_missingHeader_returnsUnauthorized() throws Exception {
//        mockMvc.perform(post("/auth/logout"))
//                .andExpect(status().isUnauthorized())
//                .andExpect(jsonPath("$.error").value("Token inválido"));
//    }
//}
