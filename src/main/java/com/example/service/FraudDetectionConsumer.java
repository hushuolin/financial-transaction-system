package com.example.service;

import com.example.model.Transaction;
import com.example.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudDetectionConsumer {
    private final TransactionRepository transactionRepository;

    @KafkaListener(topics = "transactions", groupId = "fraud-detection-group")
    public void consumeTransaction(ConsumerRecord<String, Transaction> record) {
        Transaction transaction = record.value();
        log.info("Processing Transaction from Kafka: {}", transaction);

        // ✅ Remove duplicate fraud detection logic (Kafka Streams handles it now)
        transactionRepository.save(transaction);  // Still store all transactions

        log.info("✅ Transaction processed: {}", transaction);
    }

    @KafkaListener(topics = "risk-alerts", groupId = "fraud-detection-group")
    public void handleRiskAlerts(Transaction transaction) {
        log.warn("🚨 High-Risk Transaction Detected: {}", transaction);
        // ✅ Here, you can extend this later (e.g., notify fraud teams)
    }
}
