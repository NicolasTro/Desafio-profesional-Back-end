package com.backend.auth_service.smoke;

import com.backend.auth_service.client.AccountsClient;
import com.backend.auth_service.model.dto.AccountResponseDTO;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Tag("smoke")
public class AuthSmokeTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.baseURI = "http://localhost";
        System.out.println("Test base URI: " + RestAssured.baseURI + ":" + RestAssured.port);
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public AccountsClient accountsClient() {
            AccountsClient mockClient = mock(AccountsClient.class);
            AccountResponseDTO mockResponse = new AccountResponseDTO();
            mockResponse.setId("mock-id");
            mockResponse.setCvu("1234567890123456789012");
            mockResponse.setAlias("mock.alias.mock");
            when(mockClient.createAccount(any())).thenReturn(mockResponse);
            return mockClient;
        }
    }

    @Test
    void application_shouldBeRunning() {
        given()
        .when()
            .get("/")
        .then()
            .statusCode(anyOf(is(403), is(404))); // 403 is expected due to security, 404 if no root mapping
    }

    @Test
    void login_shouldReturn404_whenUserDoesNotExist() {
        String body = """
            {
              "email": "juan@example.com",
              "password": "123456"
            }
            """;

        given()
            .contentType("application/json")
            .body(body)
        .when()
            .post("/auth/login")
        .then()
            .statusCode(404); // User not found
    }

    @Test
    void logout_shouldReturn200_whenCalled() {
        // Logout should be accessible even without authentication since it's in permitAll
        given()
        .when()
            .post("/auth/logout")
        .then()
            .statusCode(200);
    }
}