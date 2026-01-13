package com.DigitalLibraryPlatform.Book.Lending.Servicd.kafka;

import com.DigitalLibraryPlatform.Book.Lending.Servicd.event.BorrowEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BorrowEventPublisher {


    @Autowired
    private KafkaTemplate<String, BorrowEvent> kafkaTemplate;


    public void publish(BorrowEvent event){
        kafkaTemplate.send(TopicConfig.BOOK_LENDING_EVENTS, event.getBorrowRecordId().toString(), event);
    }
}
