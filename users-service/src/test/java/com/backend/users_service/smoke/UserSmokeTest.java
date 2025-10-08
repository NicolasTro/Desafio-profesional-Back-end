package com.backend.users_service.smoke;

import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.context.annotation.Import;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Import(com.backend.users_service.config.TestConfig.class)
@Tag("smoke")
public class UserSmokeTest {

    @org.springframework.boot.test.mock.mockito.MockBean(com.backend.users_service.filter.InternalKeyFilter.class)
    private com.backend.users_service.filter.InternalKeyFilter internalKeyFilter;

    @org.springframework.boot.test.mock.mockito.MockBean(com.backend.users_service.client.AccountsClient.class)
    private com.backend.users_service.client.AccountsClient accountsClient;

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        // Make sure mock filter forwards the request
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

        // RestAssured may fail if server doesn't set content-type; relax parser
        io.restassured.RestAssured.defaultParser = io.restassured.parsing.Parser.JSON;

    // Ensure the embedded server uses a mocked AccountsClient that returns predictable account data
    com.backend.users_service.model.dto.AccountResponseDTO dto = new com.backend.users_service.model.dto.AccountResponseDTO();
    dto.setAlias("alias.test.1");
    dto.setCvu("0000000000000000000000");
    org.mockito.Mockito.when(accountsClient.getAccountByUserId(org.mockito.Mockito.anyString()))
        .thenReturn(dto);
    }

    @Test
    void registerUser_shouldReturn200_andCreateUser() {
        String body = String.format("{\n  \"userId\": \"%s\",\n  \"nombre\": \"Juan\",\n  \"apellido\": \"Pérez\",\n  \"dni\": \"12345678\",\n  \"email\": \"juan@example.com\",\n  \"telefono\": \"099123456\"\n}", UUID.randomUUID().toString());

        // First create the user
        String userId =
        given()
            .header("X-Internal-Key", "dmh-internal-key-please-change")
            .contentType("application/json")
            .body(body)
        .when()
            .post("/users")
        .then()
            .statusCode(200)
            .extract()
            .jsonPath()
            .getString("userId");

        // Then fetch the user to get account info (accountsClient is mocked in TestConfig)
        given()
            .header("X-Internal-Key", "dmh-internal-key-please-change")
            .contentType("application/json")
        .when()
            .get("/users/" + userId)
        .then()
            .statusCode(200)
            .body("cvu", hasLength(22))
            .body("alias", matchesRegex("\\w+\\.\\w+\\.\\w+"));
    }
}