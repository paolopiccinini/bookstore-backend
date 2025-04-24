package com.example.bookstore.exception;

public class UserAlreadyPresentException extends RuntimeException {

    public UserAlreadyPresentException(String username) {
        super("User alredy present: " + username);
    }
    
}
