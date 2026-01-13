package com.DigitalLibraryPlatform.Book.Lending.Servicd.health;

import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;

@Component
public class BorrowingHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    public BorrowingHealthIndicator(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            return Health.up()
                    .withDetail("borrowing-db", "PostgreSQL is reachable")
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("borrowing-db", "PostgreSQL is DOWN")
                    .withException(e)
                    .build();
        }
    }
}
