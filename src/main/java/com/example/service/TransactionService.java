package com.example.service;

import com.example.model.Transaction;
import com.example.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TransactionService {
    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    private final TransactionRepository transactionRepository;
    private final RedisLedgerService redisLedgerService;  // ✅ Inject Redis service
    private static final String TRANSACTION_TOPIC = "transactions";

    public Transaction processTransaction(String accountId, double amount, String type) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero");
        }

        // Store transaction in PostgreSQL
        Transaction transaction = new Transaction(null, UUID.randomUUID().toString(), accountId, amount, type, false);
        transactionRepository.save(transaction);

        // ✅ Update Redis balance
        double signedAmount = type.equalsIgnoreCase("withdrawal") ? -amount : amount;
        redisLedgerService.updateBalance(accountId, signedAmount);

        // ✅ Send transaction to Kafka
        kafkaTemplate.send(TRANSACTION_TOPIC, transaction);
        
        return transaction;
    }

    public Optional<Transaction> getTransactionById(Long id) {
        return transactionRepository.findById(id);
    }
}
