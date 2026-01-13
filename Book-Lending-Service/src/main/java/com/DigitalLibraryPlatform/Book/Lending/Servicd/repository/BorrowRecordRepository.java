package com.DigitalLibraryPlatform.Book.Lending.Servicd.repository;

import com.DigitalLibraryPlatform.Book.Lending.Servicd.BorrowStatus;
import com.DigitalLibraryPlatform.Book.Lending.Servicd.entity.BorrowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowRecordRepository extends JpaRepository<BorrowRecord, UUID> {

   //Rule: max 2 active book per member
    int countByMemberIdAndStatus(UUID memberId, BorrowStatus status);

    //Rule: prevent double lending of same book
    boolean existsByBookIdAndStatus(UUID bookId, BorrowStatus status);


    // Used for return / renew
    Optional<BorrowRecord> findByMemberIdAndBookIdAndStatus(
            UUID memberId,
            UUID bookId,
            BorrowStatus status
    );

    // Used for cooldown rule
    Optional<BorrowRecord> findTopByMemberIdAndBookIdAndStatusOrderByReturnedAtDesc(
            UUID memberId,
            UUID bookId,
            BorrowStatus status
    );

}
