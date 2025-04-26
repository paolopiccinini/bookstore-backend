package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookType;
import com.example.bookstore.entity.NewReleaseBook;
import com.example.bookstore.entity.OldEditionBook;
import com.example.bookstore.entity.RegularBook;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.repository.BookRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookServiceImplTest {

    @Mock
    private BookRepository bookRepo;
    @InjectMocks
    private BookServiceImpl sut;

    private BookDto bookRequest;

    @BeforeEach
    public void setUp() {
        bookRequest = new BookDto();
        bookRequest.setBasePrice(1.0);
        bookRequest.setBookType(BookType.NEW_RELEASE);
        bookRequest.setAuthor("author");
        bookRequest.setIsbn("123");
        bookRequest.setTitle("title");
    }

    @Test
    @DisplayName("GIVEN a BookRequest WHEN createBook THEN the book is created")
    void createBookTest() {
        var book = bookRequest.convertToBook();
        when(bookRepo.save(eq(book))).thenReturn(book);

        var result = sut.createBook(bookRequest);
        Assertions.assertEquals(book.convertToDto(), result);
    }

    @Test
    @DisplayName("GIVEN the book is found WHEN updateBook THEN the old book is deleted and a new book is created")
    void updateBookOk() {
        var book = bookRequest.convertToBook();
        when(bookRepo.findByIsbn(eq(bookRequest.getIsbn()))).thenReturn(Optional.of(book));
        var newBook = new OldEditionBook();
        newBook.setBasePrice(bookRequest.getBasePrice());
        newBook.setAuthor(bookRequest.getAuthor());
        newBook.setIsbn(bookRequest.getIsbn());
        newBook.setTitle(bookRequest.getTitle());
        when(bookRepo.save(eq(newBook))).thenReturn(newBook);

        var result = sut.updateBook(bookRequest);
        verify(bookRepo).delete(eq(book));
        Assertions.assertEquals(newBook.convertToDto(), result);
    }

    @Test
    @DisplayName("GIVEN the book is not found WHEN updateBook THEN BookNotFoundException is thrown")
    void updateBookBookNotFoundException() {
        when(bookRepo.findByIsbn(eq(bookRequest.getIsbn()))).thenThrow(new BookNotFoundException(bookRequest.getIsbn()));

        var exception = Assertions.assertThrows(BookNotFoundException.class, () -> sut.updateBook(bookRequest));
        Assertions.assertEquals("Book not found with isbn 123", exception.getMessage());
    }

    @Test
    @DisplayName("GIVEN an isbn WHEN deleteBook THEN the repo deleteByIsbn is called with the sbn")
    void deleteBookTest() {
        sut.deleteBook("123");
        verify(bookRepo).deleteByIsbn(eq("123"));
    }

    @Test
    @DisplayName("GIVEN a valid isbn WHEN getBook THEN the book is retrieved")
    void getBookOk() {
        var book = bookRequest.convertToBook();
        when(bookRepo.findByIsbn(eq(bookRequest.getIsbn()))).thenReturn(Optional.of(book));

        var result = sut.getBook(bookRequest.getIsbn());
        Assertions.assertEquals(book.convertToDto(), result);

    }

    @Test
    @DisplayName("GIVEN a invalid isbn WHEN getBook THEN BookNotFoundException is thrown")
    void getBookBookNotFoundException() {
        when(bookRepo.findByIsbn(eq(bookRequest.getIsbn()))).thenThrow(new BookNotFoundException(bookRequest.getIsbn()));

        var exception = Assertions.assertThrows(BookNotFoundException.class, () -> sut.getBook(bookRequest.getIsbn()));
        Assertions.assertEquals("Book not found with isbn 123", exception.getMessage());
    }

    @Test
    @DisplayName("")
    void getFilteredBooksTest() {
        when(bookRepo.findAll(any(Specification.class), any(Pageable.class))).thenReturn(new PageImpl<>(List.of(new NewReleaseBook(), new OldEditionBook(), new RegularBook()), Pageable.ofSize(10), 3));
        var newBook = new BookDto();
        newBook.setBookType(BookType.NEW_RELEASE);
        var oldBook = new BookDto();
        oldBook.setBookType(BookType.OLD_EDITION);
        var regularBook = new BookDto();
        regularBook.setBookType(BookType.REGULAR);
        var expected = new PageImpl<>(List.of(newBook, oldBook, regularBook), Pageable.ofSize(10), 3);

        var result = sut.getFilteredBooks(null, null, null, null, null, Pageable.ofSize(10));
        Assertions.assertEquals(expected, result);
    }
}
