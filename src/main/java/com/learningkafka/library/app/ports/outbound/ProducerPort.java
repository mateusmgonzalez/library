package com.learningkafka.library.app.ports.outbound;

import com.learningkafka.library.app.domain.Library;

public interface ProducerPort {



    void publish(Library event);



}
