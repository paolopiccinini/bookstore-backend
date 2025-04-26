package com.example.bookstore.dto;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.NewReleaseBook;
import com.example.bookstore.entity.OldEditionBook;
import com.example.bookstore.entity.RegularBook;
import lombok.Getter;

@Getter
public enum BookType {
    NEW_RELEASE(NewReleaseBook.class) {

        @Override
        public Book createBookInstance(BookDto request) {
            return new NewReleaseBook(request);
        }
       
    },
    REGULAR(RegularBook.class) {

        @Override
        public Book createBookInstance(BookDto request) {
            return new RegularBook(request);
        }

    },
    OLD_EDITION(OldEditionBook.class) {

        @Override
        public Book createBookInstance(BookDto request) {
            return new OldEditionBook(request);
        }

    };

    BookType(Class<? extends Book> clazz) {
        this.clazz = clazz;
    }

    private Class<? extends Book> clazz;

    public abstract Book createBookInstance(BookDto request);
    
}
