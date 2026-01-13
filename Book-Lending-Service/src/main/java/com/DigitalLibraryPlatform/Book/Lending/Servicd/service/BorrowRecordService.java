package com.DigitalLibraryPlatform.Book.Lending.Servicd.service;

import com.DigitalLibraryPlatform.Book.Lending.Servicd.BorrowStatus;
import com.DigitalLibraryPlatform.Book.Lending.Servicd.DTO.InventoryResponse;
import com.DigitalLibraryPlatform.Book.Lending.Servicd.client.InventoryClient;
import com.DigitalLibraryPlatform.Book.Lending.Servicd.entity.BorrowRecord;
import com.DigitalLibraryPlatform.Book.Lending.Servicd.event.BorrowEvent;
import com.DigitalLibraryPlatform.Book.Lending.Servicd.event.BorrowEventType;
import com.DigitalLibraryPlatform.Book.Lending.Servicd.kafka.BorrowEventPublisher;
import com.DigitalLibraryPlatform.Book.Lending.Servicd.repository.BorrowRecordRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;


@Service
@Transactional
public class BorrowRecordService {


    private BorrowRecordRepository borrowRecordRepository;
    private BorrowEventPublisher borrowEventPublisher;
    private final InventoryClient inventoryClient;


    public BorrowRecordService(BorrowRecordRepository borrowRecordRepository, BorrowEventPublisher borrowEventPublisher, InventoryClient inventoryClient) {
        this.borrowRecordRepository = borrowRecordRepository;
        this.borrowEventPublisher = borrowEventPublisher;
        this.inventoryClient = inventoryClient;
    }

    public void borrowBook(UUID memberId, UUID bookId) {


        //Rule 1: Max 2 active books per user
        int activeCount = borrowRecordRepository
                .countByMemberIdAndStatus(memberId, BorrowStatus.ACTIVE);

        if(activeCount >= 2)
        {
            throw new IllegalStateException("Member already has max books");
        }


        //Rule 2: Book should not be already borrowed
        boolean bookAlreadyBorrowed =
                borrowRecordRepository.existsByBookIdAndStatus(bookId, BorrowStatus.ACTIVE);

        if (bookAlreadyBorrowed) {
            throw new IllegalStateException("Book already borrowed by another member");
        }


        //Rule 3: Cooldown check (30 days)
        Optional<BorrowRecord> lastReturned =
                borrowRecordRepository.findTopByMemberIdAndBookIdAndStatusOrderByReturnedAtDesc(memberId, bookId, BorrowStatus.RETURNED);

        if(lastReturned.isPresent()) {
            LocalDateTime lastReturnedAt = lastReturned.get().getReturnedAt();
            if(lastReturnedAt.plusDays(30).isAfter(LocalDateTime.now())) {
                throw new IllegalStateException("Cooldown period not completed");
            }
        }

        InventoryResponse inventory =
                inventoryClient.getBookAvailability(bookId.toString()).block();

        if (inventory == null || !inventory.isActive()) {
            throw new IllegalStateException("Book is not active");
        }

        if (inventory.getAvailableCopies() <= 0) {
            throw new IllegalStateException("No available copies for this book");
        }


        //Create new borrow record
        BorrowRecord record = new BorrowRecord();
        record.setMemberId(memberId);
        record.setBookId(bookId);
        record.setStatus(BorrowStatus.ACTIVE);
        record.setIssuedAt(LocalDateTime.now());
        record.setDueAt(LocalDateTime.now().plusDays(7));
        record.setRenewalCount(0);

        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        BorrowEvent event = new BorrowEvent(
                BorrowEventType.BOOK_BORROWED,
                savedRecord.getId(),
                savedRecord.getBookId(),
                savedRecord.getMemberId()
        );

        publishEventAfterCommit(event);

    }


    private void publishEventAfterCommit(BorrowEvent event) {
        if(TransactionSynchronizationManager.isActualTransactionActive()) {
            TransactionSynchronizationManager.registerSynchronization(
                    new TransactionSynchronization() {
                        @Override
                        public void afterCommit() {
                            borrowEventPublisher.publish(event);
                        }
                    }
            );
        } else {
            borrowEventPublisher.publish(event);
        }
    }


    public void returnBook(UUID memberId, UUID bookId) {

        BorrowRecord record = borrowRecordRepository
                .findByMemberIdAndBookIdAndStatus(memberId, bookId, BorrowStatus.ACTIVE)
                .orElseThrow(()->
                        new IllegalStateException("No active borrow record found"));

        record.setStatus(BorrowStatus.RETURNED);
        record.setReturnedAt(LocalDateTime.now());

        BorrowRecord savedRecord = borrowRecordRepository.save(record);

        BorrowEvent event = new BorrowEvent(
                BorrowEventType.BOOK_RETURNED,
                savedRecord.getId(),
                savedRecord.getBookId(),
                savedRecord.getMemberId()
        );

        publishEventAfterCommit(event);
    }




    public void renewBook(UUID memberId, UUID bookId ) {
        BorrowRecord record = borrowRecordRepository
                .findByMemberIdAndBookIdAndStatus(memberId, bookId, BorrowStatus.ACTIVE)
                .orElseThrow(() ->
                        new IllegalStateException("No active borrow record found"));


        if(record.getRenewalCount() >= 1) {
            throw new IllegalStateException("Renewal limit exceeded");
        }

        record.setDueAt(record.getDueAt().plusDays(7));
        record.setRenewalCount(record.getRenewalCount() + 1);

        borrowRecordRepository.save(record);
    }

}
