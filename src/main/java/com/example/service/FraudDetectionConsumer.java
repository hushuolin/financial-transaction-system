package com.example.service;

import com.example.model.Transaction;
import com.example.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionConsumer {
    private final TransactionRepository transactionRepository;
    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    private static final double FRAUD_THRESHOLD = 5000.0;
    private static final String FRAUD_ALERT_TOPIC = "fraud-alerts";

    @KafkaListener(topics = "transactions", groupId = "fraud-detection-group")
    public void consumeTransaction(Transaction transaction) {
        log.info("✅ Transaction consumed: {}", transaction);

        // Check for fraud
        if (transaction.getAmount() > FRAUD_THRESHOLD) {
            log.warn("🚨 Fraud detected: {}", transaction);
            markAsFraudulent(transaction);
        }
    }

    private void markAsFraudulent(Transaction transaction) {
        Optional<Transaction> existingTransaction = transactionRepository.findByTransactionId(transaction.getTransactionId());

        if (existingTransaction.isPresent() && !existingTransaction.get().isFraudulent()) {
            Transaction fraudTransaction = existingTransaction.get();
            fraudTransaction.setFraudulent(true);
            transactionRepository.save(fraudTransaction); // Update fraud status in DB

            // Publish fraud transaction to fraud-alerts Kafka topic
            kafkaTemplate.send(FRAUD_ALERT_TOPIC, fraudTransaction);
            log.warn("🚀 Fraud status updated in DB & sent to Kafka: {}", fraudTransaction);
        }
    }
}
