package com.example.bookstore.service;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookType;
import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.repository.BookRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepo;

    @Override
    @Transactional
    public BookDto createBook(BookDto book) {
        return bookRepo.save(book.convertToBook()).convertToDto();
    }

    @Override
    @Transactional
    public BookDto updateBook(BookDto book) {
        Book existingBook = bookRepo.findByIsbn(book.getIsbn())
                .orElseThrow(() -> new BookNotFoundException(book.getIsbn()));
        log.info("Book found deleting");
        bookRepo.delete(existingBook);
        bookRepo.flush();
        log.info("Creating new book");
        return bookRepo.save(book.convertToBook()).convertToDto();
    }


    @Override
    @Transactional
    public void deleteBook(String isbn) {
        bookRepo.deleteByIsbn(isbn);
    }

    @Override
    @Transactional(readOnly = true)
    public BookDto getBook(String isbn) {
        log.info("Serarching book");
        return bookRepo.findByIsbn(isbn).map(Book::convertToDto).orElseThrow(() -> new BookNotFoundException(isbn));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookDto> getFilteredBooks(String title, String author, BookType bookType,
                                          Double minPrice, Double maxPrice, Pageable pageable) {
        Specification<Book> spec = Specification.where(null);

        if (title != null) {
            spec = spec.and((root, _, cb) -> cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%"));
        }
        if (author != null) {
            spec = spec.and((root, _, cb) -> cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%"));
        }
        if (bookType != null) {
            spec = spec.and((root, _, cb) -> cb.equal(root.type(), bookType.getClazz()));
        }
        if (minPrice != null) {
            spec = spec.and((root, _, cb) -> cb.greaterThanOrEqualTo(root.get("basePrice"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((root, _, cb) -> cb.lessThanOrEqualTo(root.get("basePrice"), maxPrice));
        }
        log.info("Searching books");
        var bookPage = bookRepo.findAll(spec, pageable);
        log.info("Found: {}", bookPage);
        log.info("Converting into DTO");
        var bookResponses = bookPage.getContent().stream()
            .map(Book::convertToDto)
            .collect(Collectors.toList());
        log.info("Converted");
        return new PageImpl<>(bookResponses, pageable, bookPage.getTotalElements());
    }
}
