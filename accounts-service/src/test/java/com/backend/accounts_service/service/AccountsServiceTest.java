package com.backend.accounts_service.service;

import com.backend.accounts_service.client.CardsClient;
import com.backend.accounts_service.client.TransactionsClient;
import com.backend.accounts_service.model.domain.Account;
import com.backend.accounts_service.model.dto.AccountCreateDTO;
import com.backend.accounts_service.model.dto.AccountResponseDTO;
import com.backend.accounts_service.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountsServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private TransactionsClient transactionsClient;

    @Mock
    private CardsClient cardsClient;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setup() {
        // no-op
    }

    @Test
    void createAccount_shouldSaveAndReturnDTO_whenUserHasNoAccount() {
        AccountCreateDTO dto = new AccountCreateDTO();
        dto.setUserId("user-1");

        when(accountRepository.findByUserId("user-1")).thenReturn(Optional.empty());
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> {
            Account a = invocation.getArgument(0);
            a.setId("generated-id");
            return a;
        });

        AccountResponseDTO response = accountService.createAccount(dto);

        assertNotNull(response);
        assertEquals("user-1", response.getUserId());
        assertNotNull(response.getCvu());
        assertEquals(22, response.getCvu().length());
        verify(accountRepository).save(any(Account.class));
    }

    @Test
    void createAccount_shouldThrow_whenUserAlreadyHasAccount() {
        AccountCreateDTO dto = new AccountCreateDTO();
        dto.setUserId("user-1");

        Account existing = new Account();
        existing.setUserId("user-1");

        when(accountRepository.findByUserId("user-1")).thenReturn(Optional.of(existing));

        assertThrows(RuntimeException.class, () -> accountService.createAccount(dto));
        verify(accountRepository, never()).save(any(Account.class));
    }

    @Test
    void updateBalance_creditAndDebit_behaveCorrectly() {
        Account acc = new Account();
        acc.setCvu("1111111111111111111111");
        acc.setBalance(100.0);

        when(accountRepository.findByCvu("1111111111111111111111")).thenReturn(Optional.of(acc));
        when(accountRepository.save(any(Account.class))).thenAnswer(invocation -> invocation.getArgument(0));

        accountService.updateBalance("1111111111111111111111", 50.0, "CREDIT");
        assertEquals(150.0, acc.getBalance());

        accountService.updateBalance("1111111111111111111111", 20.0, "DEBIT");
        assertEquals(130.0, acc.getBalance());

        assertThrows(RuntimeException.class, () -> accountService.updateBalance("1111111111111111111111", 1000.0, "DEBIT"));
    }
}
