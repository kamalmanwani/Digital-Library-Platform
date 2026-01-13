package com.DigitalLibraryPlatform.Book.Lending.Servicd.entity;

import com.DigitalLibraryPlatform.Book.Lending.Servicd.BorrowStatus;
import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "borrow_records",
        indexes = {
                @Index(name = "idx_member_status", columnList = "memberId,status"),
                @Index(name = "idx_book_status", columnList = "bookId,status")
        }
)
@Data
public class BorrowRecord {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID bookId;

    @Column(nullable = false)
    private UUID memberId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BorrowStatus status;

    @Column(nullable = false)
    private LocalDateTime issuedAt;

    @Column(nullable = false)
    private LocalDateTime dueAt;

    private LocalDateTime returnedAt;

    @Column(nullable = false)
    private Integer renewalCount;

    @Version
    private Long version;
}
