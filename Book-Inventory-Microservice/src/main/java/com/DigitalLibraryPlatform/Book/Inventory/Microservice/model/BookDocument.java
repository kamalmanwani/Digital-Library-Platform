package com.DigitalLibraryPlatform.Book.Inventory.Microservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "books")
@Data
public class BookDocument {


    @Id
    private String bookId;

    @Indexed
    private String title;

    @Indexed
    private String author;

    private int totalCopies;
    private int availableCopies;
    private boolean active;

    private Instant createdAt;

}
