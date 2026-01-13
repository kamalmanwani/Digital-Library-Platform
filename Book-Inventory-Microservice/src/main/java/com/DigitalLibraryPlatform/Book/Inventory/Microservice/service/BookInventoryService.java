package com.DigitalLibraryPlatform.Book.Inventory.Microservice.service;

import com.DigitalLibraryPlatform.Book.Inventory.Microservice.DTO.BookResponse;
import com.DigitalLibraryPlatform.Book.Inventory.Microservice.DTO.CreateBookRequest;
import com.DigitalLibraryPlatform.Book.Inventory.Microservice.model.BookDocument;
import com.DigitalLibraryPlatform.Book.Inventory.Microservice.repository.BookRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class BookInventoryService {

    private final BookRepository repository;
    private final MongoTemplate mongoTemplate;

    public BookInventoryService(BookRepository repository, MongoTemplate mongoTemplate) {
        this.repository = repository;
        this.mongoTemplate = mongoTemplate;
    }

    public BookDocument create(CreateBookRequest request) {

        BookDocument book = new BookDocument();

        book.setBookId(UUID.randomUUID().toString());
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());
        book.setActive(true);
        book.setCreatedAt(Instant.now());

        return repository.save(book);
    }

    public BookResponse toResponse(BookDocument book) {

        BookResponse response = new BookResponse();
        response.setBookId(book.getBookId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setAvailableCopies(book.getAvailableCopies());
        response.setActive(book.isActive());

        return response;
    }

    @Cacheable(value = "books", key = "#bookId")
    public Optional<BookResponse> getById(String bookId) {
        return repository.findById(bookId)
                .map(this::toResponse);
    }

    //SEARCH BY TITLE
    @Cacheable(value = "booksByTitle", key = "#title.toLowerCase()")
    public List<BookResponse> searchByTitle(String title) {

        List<BookDocument> documents =
                repository.findByTitleContainingIgnoreCase(title);

        List<BookResponse> responses = new ArrayList<>();

        for (BookDocument document : documents) {
            responses.add(toResponse(document));
        }

        return responses;
    }

    //SEARCH BY AUTHOR
    @Cacheable(value = "booksByAuthor", key = "#author.toLowerCase()")
    public List<BookResponse> searchByAuthor(String author) {

        List<BookDocument> documents =
                repository.findByAuthorContainingIgnoreCase(author);

        List<BookResponse> responses = new ArrayList<>();

        for (BookDocument document : documents) {
            responses.add(toResponse(document));
        }

        return responses;
    }
    @CacheEvict(
            value = { "books", "booksByTitle", "booksByAuthor" },
            allEntries = true
    )
    public void handleBookIssued(String bookId) {


        Query query = new Query();
        query.addCriteria(
                Criteria.where("bookId").is(bookId)
                        .and("availableCopies").gt(0)
                        .and("active").is(true)
        );

        Update update = new Update();
        update.inc("availableCopies", -1);

        mongoTemplate.updateFirst(query, update, BookDocument.class);
    }



    @CacheEvict(
            value = { "books", "booksByTitle", "booksByAuthor" },
            allEntries = true
    )
    public void handleBookReturned(String bookId) {

        Query query = new Query();
        query.addCriteria(
                Criteria.where("bookId").is(bookId)
                        .and("availableCopies").lt("totalCopies")
                        .and("active").is(true)
        );

        Update update = new Update();
        update.inc("availableCopies", 1);

        mongoTemplate.updateFirst(query, update, BookDocument.class);
    }

}
