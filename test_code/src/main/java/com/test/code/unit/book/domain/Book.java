package com.test.code.unit.book.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Book {
    private String author;
    private String title;
    private String publisher;

    public String makeDescription() {
        return String.format("%s가 집필한 최고의 저서 [%s] 공급 : %s", author, title, publisher);
    }
}
