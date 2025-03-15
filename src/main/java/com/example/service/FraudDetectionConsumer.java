package com.example.service;

import com.example.model.Transaction;
import com.example.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionConsumer {
    private final TransactionRepository transactionRepository;
    private static final double FRAUD_THRESHOLD = 5000.0;

    @KafkaListener(topics = "transactions", groupId = "fraud-detection-group")
    public void consumeTransaction(Transaction transaction) {
        log.info("Processing Transaction from Kafka: {}", transaction);

        // Check if transaction is fraudulent
        if (transaction.getAmount() > FRAUD_THRESHOLD) {
            transaction.setFraudulent(true);
            // Save only if fraud detected
            transactionRepository.save(transaction); 
            log.warn("🚨 Fraud detected: {}", transaction);
        } else {
            log.info("✅ Transaction processed normally: {}", transaction);
        }
        
    }
}
