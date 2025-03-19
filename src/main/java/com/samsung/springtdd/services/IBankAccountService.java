package com.samsung.springtdd.services;

import com.samsung.springtdd.models.BankAccount;
import org.springframework.stereotype.Service;

@Service
public interface IBankAccountService {
    BankAccount drawMoney(String accountNumber, double balance);
}
