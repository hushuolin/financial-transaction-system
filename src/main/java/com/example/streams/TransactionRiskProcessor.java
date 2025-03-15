package com.example.streams;

import com.example.model.Transaction;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Produced;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Slf4j
@Component
public class TransactionRiskProcessor {

    private static final String TRANSACTIONS_TOPIC = "transactions";
    private static final String RISK_ALERTS_TOPIC = "risk-alerts";
    private static final double FRAUD_THRESHOLD = 5000.0;
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public TransactionRiskProcessor() {
        Properties props = new Properties();
        props.put("application.id", "transaction-risk-assessment");
        props.put("bootstrap.servers", "localhost:9092");
        props.put("default.key.serde", Serdes.String().getClass().getName());
        props.put("default.value.serde", Serdes.String().getClass().getName());

        StreamsBuilder builder = new StreamsBuilder();
        KStream<String, String> transactionsStream = builder.stream(TRANSACTIONS_TOPIC);

        transactionsStream
            .filter((key, value) -> isHighRiskTransaction(value))  // ✅ Filter high-value transactions
            .to(RISK_ALERTS_TOPIC, Produced.with(Serdes.String(), Serdes.String())); // ✅ Send to risk-alerts

        Topology topology = builder.build();
        KafkaStreams streams = new KafkaStreams(topology, props);
        streams.start();

        // ✅ Ensure proper shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(streams::close));
    }

    private boolean isHighRiskTransaction(String transactionJson) {
        try {
            Transaction transaction = objectMapper.readValue(transactionJson, Transaction.class);
            return transaction.getAmount() > FRAUD_THRESHOLD;
        } catch (Exception e) {
            log.error("❌ Failed to parse transaction JSON: {}", transactionJson, e);
            return false;
        }
    }
}
