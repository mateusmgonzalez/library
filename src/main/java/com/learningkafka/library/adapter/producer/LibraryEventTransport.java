package com.learningkafka.library.adapter.producer;

import com.learningkafka.library.app.domain.LibraryType;
import lombok.*;
import org.apache.avro.reflect.AvroName;

import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class LibraryEventTransport {

    @AvroName("libraryId")
    private Integer libraryEventId;

    @AvroName("libraryType")
    private String libraryType;

    @AvroName("book")
    private BookTransport book;
}