package com.learningkafka.library.adapter.controller;

import com.learningkafka.library.app.domain.Library;
import com.learningkafka.library.app.usecases.SendToUseCase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LibraryEventsController {


    private final SendToUseCase dispatcher;

    public LibraryEventsController(SendToUseCase dispatcher) {
        this.dispatcher = dispatcher;
    }


    @PostMapping("/v1/libraryevent")
    public ResponseEntity<Library> postLibraryEvent(
            @RequestBody Library library
    ) {

//        this.producerPort.publish(LibraruMapper.from(library));
        log.info("{}", library);
        dispatcher.send(library);
        return ResponseEntity.status(HttpStatus.CREATED).body(library);
    }

}
