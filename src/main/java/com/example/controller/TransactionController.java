package com.example.controller;

import com.example.model.Transaction;
import com.example.service.TransactionProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionProducer transactionProducer;

    @PostMapping
    public String createTransaction(@RequestParam String accountId, @RequestParam double amount, @RequestParam String type) {
        Transaction transaction = new Transaction(
                UUID.randomUUID().toString(),
                accountId,
                amount,
                type
        );
        transactionProducer.sendTransaction(transaction);
        return "Transaction submitted: " + transaction;
    }
}
