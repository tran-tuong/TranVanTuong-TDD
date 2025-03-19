package com.samsung.springtdd.controllers;

import com.samsung.springtdd.models.BankAccount;
import com.samsung.springtdd.services.BankAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.management.OperationsException;

@RestController
@RequestMapping("/bankaccount")
public class BankAccountController {

    @Autowired
    BankAccountService bankAccountService;

    @PostMapping("/drawmoney")
    public ResponseEntity drawMoney(@RequestBody BankAccount bankAccount)
    {
        try {
            BankAccount acc = bankAccountService.drawMoney(bankAccount.getAccountNumber(), bankAccount.getBalance());
            return ResponseEntity.ok(acc);
        }
        catch (RuntimeException e)
        {
            return ResponseEntity.ok(e.getMessage());
        }
    }

    @PostMapping("/depositmoney")
    public ResponseEntity depositMoney(@RequestBody BankAccount bankAccount) {
        try {
            BankAccount acc = bankAccountService.depositMoney(bankAccount.getAccountNumber(), bankAccount.getBalance());
            return ResponseEntity.ok(acc);
        } catch (RuntimeException e) {
            return ResponseEntity.ok(e.getMessage());
        }
    }
}
