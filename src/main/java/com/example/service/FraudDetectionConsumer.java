package com.example.service;

import com.example.model.Transaction;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FraudDetectionConsumer {

    private static final double FRAUD_THRESHOLD = 5000.0;

    @KafkaListener(topics = "transactions", groupId = "fraud-detection-group")
    public void consumeTransaction(Transaction transaction) {
        log.info("Received Transaction: {}", transaction);

        if (transaction.getAmount() > FRAUD_THRESHOLD) {
            log.warn("🚨 Fraud detected: {}", transaction);
        } else {
            log.info("✅ Transaction processed: {}", transaction);
        }
    }
}
