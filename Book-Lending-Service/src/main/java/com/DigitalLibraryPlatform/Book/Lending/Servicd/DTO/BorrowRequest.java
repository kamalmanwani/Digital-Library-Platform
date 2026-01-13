package com.DigitalLibraryPlatform.Book.Lending.Servicd.DTO;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class BorrowRequest {

    @NotNull
    private UUID memberId;

    @NotNull
    private UUID bookId;
}
