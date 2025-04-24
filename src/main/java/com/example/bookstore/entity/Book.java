package com.example.bookstore.entity;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.dto.BookResponse;

import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.DiscriminatorType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "book_type", discriminatorType = DiscriminatorType.STRING)
@Data
public abstract class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String isbn;
    private String title;
    private String author;
    private double basePrice;

    public Book(BookRequest request) {
        isbn = request.getIsbn();
        title = request.getTitle();
        author = request.getAuthor();
        basePrice = request.getBasePrice();
    }

    public abstract double calculatePrice(int numOfBooks);

    public abstract boolean isDiscountableWithLoyalty();

    public BookResponse convertToDto() {
        return new BookResponse(this);
    }

}
