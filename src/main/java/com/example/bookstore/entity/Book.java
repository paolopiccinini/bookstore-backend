package com.example.bookstore.entity;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookType;
import jakarta.persistence.*;
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

    public Book(BookDto request) {
        isbn = request.getIsbn();
        title = request.getTitle();
        author = request.getAuthor();
        basePrice = request.getBasePrice();
    }

    public abstract double calculatePrice(int numOfBooks);

    public abstract boolean isDiscountableWithLoyalty();

    public abstract BookType getType();

    public BookDto convertToDto() {
        return new BookDto(this);
    }

}
