package com.learningkafka.library.adapter.producer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.learningkafka.library.app.domain.Library;
import com.learningkafka.library.app.ports.outbound.ProducerPort;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class LibraryEventsProducer implements ProducerPort {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final KafkaTemplate<Integer, LibraryEventTransport> kafkaTemplate;

    private final ObjectMapper objectMapper;

    public LibraryEventsProducer(KafkaTemplate<Integer, LibraryEventTransport> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

//    public void sendLibraryEvent(LibraryEvent libraryEvent) throws JsonProcessingException {
//
//        var key = libraryEvent.libraryId();
//
//
//        var completableFuture = kafkaTemplate.send(topic, key,libraryEvent);
//
//        completableFuture.whenComplete((result, throwable) -> {
//            if (throwable != null) {
//                handleFailure(key, libraryEvent, throwable);
//            } else {
//                handleSuccess(key, libraryEvent, result);
//            }
//        } );
//    }

    public SendResult<Integer, LibraryEventTransport> sendLibraryEventSync(Library libraryEvent)
            throws ExecutionException, InterruptedException, TimeoutException {

        if (libraryEvent == null || libraryEvent.book() == null) {
            // Lida com casos em que objetos são nulos
            log.error("LibraryEvent or Book is null.");
            throw new IllegalArgumentException("LibraryEvent or Book cannot be null.");
        }

        BookTransport bookTransport = new BookTransport();
        bookTransport.setBookId(libraryEvent.book().bookId());
        bookTransport.setBookName(libraryEvent.book().bookName());
        bookTransport.setBookAuthor(libraryEvent.book().bookAuthor());

        LibraryEventTransport libraryEventTransport = new LibraryEventTransport();
        libraryEventTransport.setLibraryEventId(libraryEvent.libraryId());
        libraryEventTransport.setLibraryType(libraryEvent.libraryType().name().toUpperCase());
        libraryEventTransport.setBook(bookTransport);

        log.info("Sending Library Event - ID: {}, Book: {}", libraryEvent.libraryId(), bookTransport);
        log.info("LibraryEventTransport: {}", libraryEventTransport.toString());

        try {
            // Enviar o evento de biblioteca para o Kafka
            SendResult<Integer, LibraryEventTransport> result = kafkaTemplate.send(topic, libraryEventTransport)
                    .get(3, TimeUnit.SECONDS);

            // Lida com o sucesso do envio
            handleSuccess(libraryEventTransport.getLibraryEventId(), libraryEventTransport, result);

            return result;
        } catch (Exception e) {
            // Lida com exceções ao enviar para o Kafka
            log.error("Error sending LibraryEvent to Kafka", e);
            throw e;
        }
    }

    private void handleSuccess(Integer libraryEventId, LibraryEventTransport libraryEventTransport,
                               SendResult<Integer, LibraryEventTransport> result) {
        // Lógica para tratar o sucesso do envio (se necessário)
        log.info("LibraryEvent sent successfully to Kafka. ID: {}", libraryEventId);
        // Pode adicionar mais lógica aqui se necessário
    }
    private void handleFailure(Integer key, LibraryEventTransport value, Throwable throwable) {
        log.error("Error sending message and the exception is: {}", throwable.getMessage(), throwable);
    }

    @Override
    public void publish(Library library) {
        try {
           var result =  sendLibraryEventSync(library);
        } catch (ExecutionException | InterruptedException | TimeoutException e) {
            throw new RuntimeException(e);
        }
    }
}
