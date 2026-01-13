package com.DigitalLibraryPlatform.Book.Inventory.Microservice.redis;

import io.micrometer.observation.annotation.ObservationKeyValue;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class InventoryIdempotencyService {


    private final RedisTemplate<String, Object> redisTemplate;


    public InventoryIdempotencyService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    public boolean acquireEventLock(String eventId) {

        String key = "inventory-event:" +eventId;

        Boolean success = redisTemplate.opsForValue()
                .setIfAbsent(key, "PROCESSED", Duration.ofMinutes(10));

        return Boolean.TRUE.equals(success);
    }
}
