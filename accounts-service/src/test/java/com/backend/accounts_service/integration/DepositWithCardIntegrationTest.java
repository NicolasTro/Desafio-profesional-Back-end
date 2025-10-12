package com.backend.accounts_service.integration;

import com.backend.accounts_service.client.CardsClient;
import com.backend.accounts_service.client.TransactionsClient;
import com.backend.accounts_service.model.dto.TransactionRequestDTO;
import com.backend.accounts_service.model.dto.TransactionResponseDTO;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters = false)
class DepositWithCardIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionsClient transactionsClient;

    @MockBean
    private CardsClient cardsClient;

    @Test
    void whenDepositWithCard_thenTransactionsClientReceivesCardId() throws Exception {

    String createBody = "{\n" +
        "  \"userId\": \"uuid-integration-deposit\"\n" +
        "}";

    var postResult = mockMvc.perform(post("/accounts")
        .contentType(MediaType.APPLICATION_JSON)
        .content(createBody))
        .andExpect(status().isCreated())
        .andReturn();

    String accountId = new com.fasterxml.jackson.databind.ObjectMapper()
        .readTree(postResult.getResponse().getContentAsString()).get("cvu").asText();
        String cardId = "card-abc-123";


        TransactionResponseDTO resp = new TransactionResponseDTO();
        resp.setAccountId(accountId);
        resp.setDescription("Depósito con tarjeta");
        when(transactionsClient.createTransaction(any(String.class), any(TransactionRequestDTO.class))).thenReturn(resp);

        String body = "{\n" +
                "  \"amount\": 1500.0,\n" +
                "  \"description\": \"Depósito prueba\",\n" +
                "  \"origin\": \"TARJETA\",\n" +
                "  \"destination\": \"" + accountId + "\",\n" +
                "  \"cardId\": \"" + cardId + "\",\n" +
                "  \"type\": \"DEPOSIT\"\n" +
                "}";

    mockMvc.perform(post("/accounts/" + accountId + "/transferences")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());


        ArgumentCaptor<TransactionRequestDTO> captor = ArgumentCaptor.forClass(TransactionRequestDTO.class);
        verify(transactionsClient).createTransaction(any(String.class), captor.capture());

        TransactionRequestDTO sent = captor.getValue();
        assertThat(sent.getCardId()).isEqualTo(cardId);
    }
}
