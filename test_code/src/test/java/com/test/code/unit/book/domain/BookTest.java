package com.test.code.unit.book.domain;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    @Test
    void makeDescription() {
        // given
        String title = "테스트 코드";
        String author = "최준호";
        String publisher = "한국";
        Book book = Book.builder()
                .title(title)
                .author(author)
                .publisher(publisher)
                .build();

        // when
        String description = book.makeDescription();

        // then
        Assertions.assertThat(description).isEqualTo(String.format("%s가 집필한 최고의 저서 [%s] 공급 : %s", author, title, publisher));
    }
}