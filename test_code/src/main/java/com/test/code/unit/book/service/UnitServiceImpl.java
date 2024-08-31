package com.test.code.unit.book.service;

import com.test.code.unit.book.domain.Book;

public class UnitServiceImpl implements UnitService{
    @Override
    public String getDescription() {
        // db를 통해 Book 값을 가져옴
        Book book = Book.builder()
                .title("제목")
                .author("작가")
                .publisher("한국")
                .build();

        return book.makeDescription();
    }
}
