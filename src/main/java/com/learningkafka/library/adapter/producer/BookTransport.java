package com.learningkafka.library.adapter.producer;

import lombok.*;
import org.apache.avro.reflect.AvroName;

import java.util.Objects;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
@ToString
public class BookTransport {

    @AvroName("bookId")
    private Integer bookId;

    @AvroName("bookName")
    private String bookName;

    @AvroName("bookAuthor")
    private String bookAuthor;
}
