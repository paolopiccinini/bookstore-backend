package com.example.bookstore.service;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.dto.BookResponse;
import com.example.bookstore.dto.BookType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookService {
    BookResponse createBook(BookRequest book);
    BookResponse updateBook(BookRequest book);
    void deleteBook(String isbn);
    BookResponse getBook(String isbn);
    Page<BookResponse> getFilteredBooks(String title, String author, BookType bookType,
                                        Double minPrice, Double maxPrice, Pageable pageable);
}
