package com.DigitalLibraryPlatform.Book.Lending.Servicd.controller;


import com.DigitalLibraryPlatform.Book.Lending.Servicd.DTO.BorrowRequest;
import com.DigitalLibraryPlatform.Book.Lending.Servicd.service.BorrowRecordService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class BookLendingController {

    @Autowired
    private BorrowRecordService service;

    @PostMapping("/borrow")
    public ResponseEntity<String> borrow(@RequestBody @Valid BorrowRequest request) {
        service.borrowBook(request.getMemberId(), request.getBookId());
        return ResponseEntity.ok("Book borrowed successfully");
    }

    @PostMapping("/return")
    public ResponseEntity<String> returnBook(@RequestBody @Valid BorrowRequest request) {
        service.returnBook(request.getMemberId(), request.getBookId());
        return ResponseEntity.ok("Book returned successfully");
    }

    @PostMapping("/renew")
    public ResponseEntity<String> renew(@RequestBody @Valid BorrowRequest request) {
        service.renewBook(request.getMemberId(), request.getBookId());
        return ResponseEntity.ok("Book renewed successfully");
    }


}
