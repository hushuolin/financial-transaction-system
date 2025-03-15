package com.example.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String transactionId;
    private String accountId;
    private double amount;
    private String type;
    private boolean isFraudulent;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // ✅ Convert JSON string to Transaction object (for Kafka Streams processing)
    public static Transaction fromJson(String json) {
        try {
            return objectMapper.readValue(json, Transaction.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize Transaction", e);
        }
    }

    // ✅ Convert Transaction object to JSON string (if needed)
    public String toJson() {
        try {
            return objectMapper.writeValueAsString(this);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize Transaction", e);
        }
    }
}
