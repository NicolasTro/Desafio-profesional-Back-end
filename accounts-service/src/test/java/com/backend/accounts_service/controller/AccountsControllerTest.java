
package com.backend.accounts_service.controller;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import com.backend.accounts_service.service.AccountService;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AccountController.class)
@AutoConfigureMockMvc(addFilters = false)
class AccountsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @Test
    void createAccount_returnsCreated() throws Exception {
        String body = "{ \"userId\": 1 }";

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated());
    }

    @Test
    void getAccountByCvu_returnsOk() throws Exception {
        mockMvc.perform(get("/accounts/0000000000000000000000"))
                .andExpect(status().isOk());
    }

    @Test
    void getAccountsByUser_returnsOk() throws Exception {
        mockMvc.perform(get("/accounts/user/1"))
                .andExpect(status().isOk());
    }
}

