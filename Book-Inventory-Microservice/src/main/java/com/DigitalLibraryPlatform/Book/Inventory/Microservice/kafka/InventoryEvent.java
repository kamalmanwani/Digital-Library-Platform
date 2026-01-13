package com.DigitalLibraryPlatform.Book.Inventory.Microservice.kafka;

import lombok.Data;

@Data
public class InventoryEvent {

    private String eventId;
    private String eventType;
    private String bookId;
}
