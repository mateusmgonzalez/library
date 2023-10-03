package com.learningkafka.library.app.domain;

public record Book(
        Integer bookId,
        String bookName,
        String bookAuthor
) {
}
