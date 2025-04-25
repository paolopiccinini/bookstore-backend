package com.example.bookstore.service;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.dto.BookResponse;
import com.example.bookstore.dto.BookType;
import com.example.bookstore.entity.Book;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.repository.BookRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepo;

    @Override
    @Transactional
    public BookResponse createBook(BookRequest book) {
        return bookRepo.save(book.convertToBook()).convertToDto();
    }

    @Override
    @Transactional
    public BookResponse updateBook(BookRequest book) {
        Book existingBook = bookRepo.findByIsbn(book.getIsbn())
                .orElseThrow(() -> new BookNotFoundException(book.getIsbn()));
        bookRepo.delete(existingBook);
        bookRepo.flush();
        return bookRepo.save(book.convertToBook()).convertToDto();
    }


    @Override
    @Transactional
    public void deleteBook(String isbn) {
        bookRepo.deleteByIsbn(isbn);
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBook(String isbn) {
        return bookRepo.findByIsbn(isbn).map(Book::convertToDto).orElseThrow(() -> new BookNotFoundException(isbn));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookResponse> getFilteredBooks(String title, String author, BookType bookType,
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

        var bookPage = bookRepo.findAll(spec, pageable);
        var bookResponses = bookPage.getContent().stream()
            .map(Book::convertToDto)
            .collect(Collectors.toList());

        return new PageImpl<>(bookResponses, pageable, bookPage.getTotalElements());
    }
}
