package com.backend.accounts_service.integration;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class AccountsIntegrationTest {

    @Autowired
    private MockMvc mockMvc;


    @Test
    void createAccountIntegration_shouldReturnCreated() throws Exception {
    String body = "{\n" +
        "  \"userId\": \"uuid-usuario-123\",\n" +
        "  \"alias\": \"juan.perez.dmh\",\n" +
        "  \"currency\": \"ARS\"\n" +
        "}";

    mockMvc.perform(post("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isCreated());
    }


    @Test
    void getAccountByCvuIntegration_shouldReturnOk() throws Exception {
        // Primero creamos la cuenta con el CVU esperado
        String body = "{\n" +
            "  \"userId\": \"uuid-test-cvu\",\n" +
            "  \"alias\": \"test.cvu.alias\",\n" +
            "  \"currency\": \"ARS\",\n" +
            "  \"cvu\": \"0000000000000000000000\"\n" +
            "}";

    var postResult = mockMvc.perform(post("/accounts")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
        .andExpect(status().isCreated())
        .andReturn();

    // Extraer el CVU real devuelto y consultarlo
    String resp = postResult.getResponse().getContentAsString();
    com.fasterxml.jackson.databind.JsonNode node = new com.fasterxml.jackson.databind.ObjectMapper().readTree(resp);
    String createdCvu = node.get("cvu").asText();

    mockMvc.perform(get("/accounts/" + createdCvu))
        .andExpect(status().isOk());
    }
}
