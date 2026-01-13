package com.DigitalLibraryPlatform.Book.Inventory.Microservice.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Component
public class InventoryHealthIndicator implements HealthIndicator {

    private final MongoTemplate mongoTemplate;

    public InventoryHealthIndicator(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Health health() {
        try {
            mongoTemplate.executeCommand("{ ping: 1 }");
            return Health.up()
                    .withDetail("inventory-db", "MongoDB is reachable")
                    .build();
        } catch (Exception ex) {
            return Health.down()
                    .withDetail("inventory-db", "MongoDB is DOWN")
                    .withException(ex)
                    .build();
        }
    }
}
