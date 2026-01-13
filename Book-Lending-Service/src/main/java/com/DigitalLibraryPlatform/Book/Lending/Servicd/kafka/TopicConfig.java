package com.DigitalLibraryPlatform.Book.Lending.Servicd.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class TopicConfig {


    public static final String BOOK_LENDING_EVENTS = "book-lending-events";

    @Bean
    public NewTopic createTopic()
    {
        return TopicBuilder.name(BOOK_LENDING_EVENTS)
                .build();
    }

}
