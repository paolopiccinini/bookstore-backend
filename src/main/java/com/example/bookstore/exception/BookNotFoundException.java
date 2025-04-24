package com.example.bookstore.exception;

public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(String isbn) {
        super("Book not found with isbn " + isbn);
    }
    
}
