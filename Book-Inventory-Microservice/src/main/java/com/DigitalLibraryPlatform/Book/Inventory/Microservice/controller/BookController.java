package com.DigitalLibraryPlatform.Book.Inventory.Microservice.controller;

import com.DigitalLibraryPlatform.Book.Inventory.Microservice.DTO.BookResponse;
import com.DigitalLibraryPlatform.Book.Inventory.Microservice.DTO.CreateBookRequest;
import com.DigitalLibraryPlatform.Book.Inventory.Microservice.model.BookDocument;
import com.DigitalLibraryPlatform.Book.Inventory.Microservice.service.BookInventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
public class BookController {

    private final BookInventoryService service;

    public BookController(BookInventoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<BookResponse> create(
            @Valid @RequestBody CreateBookRequest request) {

        BookDocument book = service.create(request);
        return ResponseEntity.ok(service.toResponse(book));
    }

    @GetMapping("/search/title")
    public ResponseEntity<List<BookResponse>> searchByTitle(
            @RequestParam String title) {

        return ResponseEntity.ok(service.searchByTitle(title));
    }

    @GetMapping("/search/author")
    public ResponseEntity<List<BookResponse>> searchByAuthor(
            @RequestParam String author) {

        return ResponseEntity.ok(service.searchByAuthor(author));
    }


    @GetMapping("/{bookId}")
    public ResponseEntity<BookResponse> get(@PathVariable String bookId) {

        return service.getById(bookId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // -------- SEARCH BOOKS --------
    @GetMapping("/search")
    public ResponseEntity<List<BookResponse>> search(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author) {

        if (title != null) {
            return ResponseEntity.ok(service.searchByTitle(title));
        }

        if (author != null) {
            return ResponseEntity.ok(service.searchByAuthor(author));
        }

        return ResponseEntity.badRequest().build();
    }
}
