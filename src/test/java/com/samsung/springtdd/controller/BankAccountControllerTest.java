package com.samsung.springtdd.controller;

import com.samsung.springtdd.controllers.BankAccountController;
import com.samsung.springtdd.models.BankAccount;
import com.samsung.springtdd.services.BankAccountService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BankAccountController.class)
@AutoConfigureMockMvc
public class BankAccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BankAccountService bankAccountService;

    @Test
    public void return_success_with_response_for_successful_draw_money() throws Exception {
        when(bankAccountService.drawMoney("ACC12345678", 50d)).thenReturn(BankAccount.builder().accountNumber("ACC12345678").balance(50).build());

        mockMvc.perform(post("/bankaccount/drawmoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountNumber\":\"ACC12345678\",\"balance\": 50}")
        ).andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber", Matchers.is("ACC12345678")))
                .andExpect(jsonPath("$.balance", Matchers.is(50.0)));
    }

    @Test
    public void return_message_account_not_exist_for_unsuccessful_draw_money() throws Exception {
        when(bankAccountService.drawMoney("ACC12345678", 50d))
                .thenThrow(new RuntimeException("Account does not exist!"));

        mockMvc.perform(post("/bankaccount/drawmoney")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"accountNumber\":\"ACC12345678\",\"balance\": 50}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account does not exist!"));
    }

    @Test
    public void return_message_account_not_enough_balance_for_unsuccessful_draw_money() throws Exception {
        when(bankAccountService.drawMoney("ACC12345678", 50d))
                .thenThrow(new RuntimeException("Balance is not enough!"));

        mockMvc.perform(post("/bankaccount/drawmoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"ACC12345678\",\"balance\": 50}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Balance is not enough!"));
    }

    @Test
    public void successfulDepositMoney() throws Exception {
        when(bankAccountService.depositMoney("ACC12345678", 50d))
                .thenReturn(BankAccount.builder().accountNumber("ACC12345678").balance(150).build());

        mockMvc.perform(post("/bankaccount/depositmoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"ACC12345678\",\"balance\": 50}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountNumber", Matchers.is("ACC12345678")))
                .andExpect(jsonPath("$.balance", Matchers.is(150.0)));
    }

    @Test
    public void return_message_account_not_exist_for_unsuccessful_deposit_money() throws Exception {
        when(bankAccountService.depositMoney("ACC99999999", 50d))
                .thenThrow(new RuntimeException("Account does not exist!"));

        mockMvc.perform(post("/bankaccount/depositmoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"ACC99999999\",\"balance\": 50}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Account does not exist!"));
    }

    @Test
    public void return_message_when_withdraw_negative_amount() throws Exception {
        when(bankAccountService.drawMoney("ACC12345678", -50d))
                .thenThrow(new RuntimeException("Withdraw amount must be greater than zero!"));

        mockMvc.perform(post("/bankaccount/drawmoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"ACC12345678\",\"balance\": -50}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Withdraw amount must be greater than zero!"));
    }

    @Test
    public void return_message_when_deposit_negative_amount() throws Exception {
        when(bankAccountService.depositMoney("ACC12345678", -50d))
                .thenThrow(new RuntimeException("Deposit amount must be greater than zero!"));

        mockMvc.perform(post("/bankaccount/depositmoney")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"accountNumber\":\"ACC12345678\",\"balance\": -50}"))
                .andExpect(status().isOk())
                .andExpect(content().string("Deposit amount must be greater than zero!"));
    }



}
