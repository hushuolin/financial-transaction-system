package com.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisLedgerService {
    private final StringRedisTemplate redisTemplate;
    private static final String BALANCE_PREFIX = "balance:";

    public void updateBalance(String accountId, double amount) {
        String balanceKey = BALANCE_PREFIX + accountId;

        // Get current balance (default to 0 if not present)
        String currentBalance = redisTemplate.opsForValue().get(balanceKey);
        double newBalance = (currentBalance != null ? Double.parseDouble(currentBalance) : 0) + amount;

        // Update balance in Redis
        redisTemplate.opsForValue().set(balanceKey, String.valueOf(newBalance), 1, TimeUnit.HOURS);
        log.info("✅ Updated Redis balance for Account {}: ${}", accountId, newBalance);
    }

    public double getBalance(String accountId) {
        String balanceKey = BALANCE_PREFIX + accountId;
        String balance = redisTemplate.opsForValue().get(balanceKey);
        return balance != null ? Double.parseDouble(balance) : 0;
    }
}
