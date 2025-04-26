package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookDto createBook(BookDto book);
    BookDto updateBook(BookDto book);
    void deleteBook(String isbn);
    BookDto getBook(String isbn);
    Page<BookDto> getFilteredBooks(String title, String author, BookType bookType,
                                   Double minPrice, Double maxPrice, Pageable pageable);
}
