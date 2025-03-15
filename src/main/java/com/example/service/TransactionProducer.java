package com.example.service;

import com.example.model.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TransactionProducer {
    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    private final String TRANSACTION_TOPIC = "transactions";

    public void sendTransaction(Transaction transaction) {
        kafkaTemplate.send(TRANSACTION_TOPIC, transaction);
    }
}
