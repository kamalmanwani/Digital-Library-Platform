package com.DigitalLibraryPlatform.Book.Lending.Servicd.client;

import com.DigitalLibraryPlatform.Book.Lending.Servicd.DTO.InventoryResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
public class InventoryClient {

    private final WebClient webClient;

    public InventoryClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public Mono<InventoryResponse> getBookAvailability(String bookId) {
        return webClient.get()
                .uri("http://localhost:8080/inventory/books/{bookId}", bookId)
                .retrieve()
                .bodyToMono(InventoryResponse.class);
    }
}
