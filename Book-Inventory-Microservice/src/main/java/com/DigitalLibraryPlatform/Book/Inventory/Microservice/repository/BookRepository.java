package com.DigitalLibraryPlatform.Book.Inventory.Microservice.repository;


import com.DigitalLibraryPlatform.Book.Inventory.Microservice.model.BookDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface BookRepository extends MongoRepository<BookDocument, String> {

    List<BookDocument> findByTitleContainingIgnoreCase(String title);

    List<BookDocument> findByAuthorContainingIgnoreCase(String author);
}
