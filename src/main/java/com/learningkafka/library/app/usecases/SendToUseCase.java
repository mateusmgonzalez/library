package com.learningkafka.library.app.usecases;

import com.learningkafka.library.app.domain.Library;
import com.learningkafka.library.app.ports.outbound.ProducerPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@RequiredArgsConstructor
@Service
public class SendToUseCase {

    private final ProducerPort producerPort;

    public void send(Library library) {
        producerPort.publish(library);
    }
}
