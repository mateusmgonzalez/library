package com.learningkafka.library.app.domain;

public record Library(
        Integer libraryId,
        LibraryType libraryType,
        Book book
) {
}
