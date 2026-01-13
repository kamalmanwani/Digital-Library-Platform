package com.DigitalLibraryPlatform.Book.Inventory.Microservice.DTO;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateBookRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String author;

    @Min(1)
    private int totalCopies;
}
