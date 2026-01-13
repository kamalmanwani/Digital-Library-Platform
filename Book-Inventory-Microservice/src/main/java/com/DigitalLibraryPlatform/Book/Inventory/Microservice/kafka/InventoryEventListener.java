package com.DigitalLibraryPlatform.Book.Inventory.Microservice.kafka;

import com.DigitalLibraryPlatform.Book.Inventory.Microservice.redis.InventoryIdempotencyService;
import com.DigitalLibraryPlatform.Book.Inventory.Microservice.service.BookInventoryService;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class InventoryEventListener {

    private final BookInventoryService inventoryService;
    private final InventoryIdempotencyService idempotencyService;


    public InventoryEventListener(BookInventoryService inventoryService, InventoryIdempotencyService idempotencyService) {
        this.inventoryService = inventoryService;
        this.idempotencyService = idempotencyService;
    }


    @KafkaListener(
            topics = "library-events",
            groupId = "book-inventory-group"
    )
    public void handleEvent(InventoryEvent event,
                            Acknowledgment acknowledgment) {

        System.out.println("RAW KAFKA MESSAGE = ");
        //Idempotency check
        boolean firstTime = idempotencyService.acquireEventLock(event.getEventId());

        if(!firstTime) {

            //Duplicate event -> do nothing
            acknowledgment.acknowledge();
            return;
        }


        System.out.println("Received event "+event.getEventType()
        + " for bookId=" +event.getBookId());


        if("BOOK_ISSUED".equals(event.getEventType())) {
            inventoryService.handleBookIssued(event.getBookId());
        }


        if ("BOOK_RETURNED".equals(event.getEventType())) {
            inventoryService.handleBookReturned(event.getBookId());
        }

        // STEP 2: Manually commit Kafka offset
        acknowledgment.acknowledge();
    }


    }

