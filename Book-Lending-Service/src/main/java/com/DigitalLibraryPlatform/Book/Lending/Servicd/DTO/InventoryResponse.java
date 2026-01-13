package com.DigitalLibraryPlatform.Book.Lending.Servicd.DTO;

import lombok.Data;

@Data
public class InventoryResponse {
    private String bookId;
    private int availableCopies;
    private boolean active;
}
