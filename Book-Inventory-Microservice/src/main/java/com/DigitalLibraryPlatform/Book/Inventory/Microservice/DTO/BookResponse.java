package com.DigitalLibraryPlatform.Book.Inventory.Microservice.DTO;

import lombok.Data;

@Data
public class BookResponse {
    private String bookId;
    private String title;
    private String author;
    private int availableCopies;
    private boolean active;
}
