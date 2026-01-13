package com.DigitalLibraryPlatform.Book.Lending.Servicd.event;

import lombok.Data;
import lombok.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Value
public class BorrowEvent {

    private UUID eventId;
    private BorrowEventType eventType;
    private UUID borrowRecordId;
    private UUID bookId;
    private UUID memberId;
    private LocalDateTime occurredAt;

    public BorrowEvent(
            BorrowEventType eventType,
            UUID borrowRecordId,
            UUID bookId,
            UUID memberId
    ) {
        this.eventId = UUID.randomUUID();
        this.eventType = eventType;
        this.borrowRecordId = borrowRecordId;
        this.bookId = bookId;
        this.memberId = memberId;
        this.occurredAt = LocalDateTime.now();
    }
}
