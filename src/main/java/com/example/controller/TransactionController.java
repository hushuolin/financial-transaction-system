package com.example.controller;

import com.example.model.Transaction;
import com.example.service.RedisLedgerService;
import com.example.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {
    private final TransactionService transactionService;
    private final RedisLedgerService redisLedgerService;

    @PostMapping
    public ResponseEntity<?> createTransaction(@RequestParam String accountId, 
                                               @RequestParam double amount, 
                                               @RequestParam String type) {
        try {
            Transaction transaction = transactionService.processTransaction(accountId, amount, type);
            return ResponseEntity.ok(transaction);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTransaction(@PathVariable Long id) {
        Optional<Transaction> transaction = transactionService.getTransactionById(id);
        return transaction.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/balance/{accountId}")
    public ResponseEntity<Double> getBalance(@PathVariable String accountId) {
        double balance = redisLedgerService.getBalance(accountId);
        return ResponseEntity.ok(balance);
    }
}
