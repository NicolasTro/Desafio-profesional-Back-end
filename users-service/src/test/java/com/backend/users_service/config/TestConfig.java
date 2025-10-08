package com.backend.users_service.config;

import com.backend.users_service.client.AccountsClient;
import com.backend.users_service.model.dto.AccountResponseDTO;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import com.backend.users_service.filter.InternalKeyFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
public class TestConfig {

    @Bean
    public AccountsClient accountsClient() {
        AccountsClient mock = Mockito.mock(AccountsClient.class);
    AccountResponseDTO dto = new AccountResponseDTO();
    dto.setAlias("alias.test.1");
    dto.setCvu("0000000000000000000000");
    Mockito.when(mock.getAccountByUserId(Mockito.anyString()))
        .thenReturn(dto);
        return mock;
    }
}
