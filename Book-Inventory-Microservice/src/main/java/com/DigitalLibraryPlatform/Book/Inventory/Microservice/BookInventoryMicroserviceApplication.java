package com.DigitalLibraryPlatform.Book.Inventory.Microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableCaching
@EnableKafka
public class BookInventoryMicroserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookInventoryMicroserviceApplication.class, args);
	}

}
