package com.samsung.springtdd.services;

import com.samsung.springtdd.models.BankAccount;
import com.samsung.springtdd.models.repository.BankAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class BankAccountService implements IBankAccountService {

    @Autowired
    BankAccountRepository bankAccountRepository;


    public BankAccount drawMoney(String accountNumber, double balance) {
        if (balance <= 0) {
            throw new RuntimeException("Withdraw amount must be greater than zero!");
        }

        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber);
        if(account != null) {
            if(account.getBalance()>=balance) {
                account.setBalance(account.getBalance() - balance);
                //Update to db
                bankAccountRepository.save(account);
                return account;
            }
            else {
                throw new RuntimeException("Balance is not enough!");
            }
        }
        else
        {
            throw new RuntimeException("Account does not exist!");
        }
    }

    public BankAccount depositMoney(String accountNumber, double amount) {
        if (amount <= 0) {
            throw new RuntimeException("Deposit amount must be greater than zero!");
        }

        BankAccount account = bankAccountRepository.findByAccountNumber(accountNumber);
        if (account != null) {
            account.setBalance(account.getBalance() + amount);
            bankAccountRepository.save(account);
            return account;
        } else {
            throw new RuntimeException("Account does not exist!");
        }
    }
}
