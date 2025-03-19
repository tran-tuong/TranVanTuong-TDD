package com.samsung.springtdd.services;

import com.samsung.springtdd.models.BankAccount;
import com.samsung.springtdd.models.repository.BankAccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class BankAccountServiceTest {
    @Mock /*Mock object for repository*/
    private BankAccountRepository bankAccountRepository;

    @InjectMocks /*Create object for service under test*/
    private BankAccountService bankAccountService;

    @Test
    void should_return_account_balance_with_remaining_money()
    {
        when(bankAccountRepository.findByAccountNumber("ACC12345678"))
                .thenReturn(BankAccount.builder().accountNumber("ACC12345678").balance(100.0).build());

        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(new BankAccount());

        BankAccount account = bankAccountService.drawMoney("ACC12345678", 50.0);

        assertNotNull(account);
        assertEquals("ACC12345678", account.getAccountNumber());
        assertEquals(50.0, account.getBalance(), 0.0);

        //Make sure that new balance value has been saved to db
        verify(bankAccountRepository, times(1)).save(account);
    }

    @Test
    void should_return_exception_for_balance_not_enought()
    {
        when(bankAccountRepository.findByAccountNumber("ACC12345678"))
                .thenReturn(BankAccount.builder().accountNumber("ACC12345678").balance(100.0).build());

        when(bankAccountRepository.save(any(BankAccount.class))).thenReturn(new BankAccount());

        assertThrows(RuntimeException.class, ()->{
            BankAccount account = bankAccountService.drawMoney("ACC12345678", 150.0);
        });

        verify(bankAccountRepository, times(0)).save(any());
    }

    @Test
    void should_return_exception_for_account_does_not_exist()
    {
        when(bankAccountRepository.findByAccountNumber("ACC12345678"))
                .thenReturn(null);

        assertThrows(RuntimeException.class, ()->{
            BankAccount account = bankAccountService.drawMoney("ACC12345678", 150.0);
        });
        verify(bankAccountRepository, times(0)).save(any());
    }
}
