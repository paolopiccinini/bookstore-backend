package com.example.bookstore.dto;

import com.example.bookstore.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class BookResponse {
    @Schema(description = "isbn of the book unique identifier", example = "97811321416")
    @NotBlank(message = "isbn can't be empty")
    @Size(min = 1, max = 13, message = "isbn must be between 1 and 13 characters long")
    private String isbn;
    @Schema(description = "title of the book", example = "Thinking in Java")
    @NotBlank(message = "title can't be empty")
    private String title;
    @NotBlank(message = "author can't be empty")
    @Schema(description = "author of the book", example = "Gang of 4")
    private String author;
    @Positive(message = "basePrice must be positive and greather then 0")
    @Schema(description = "basePrice of the book", example = "15.99")
    private double basePrice;

    public BookResponse(Book book) {
        isbn = book.getIsbn();
        title = book.getTitle();
        author = book.getAuthor();
        basePrice = book.getBasePrice();
    }
}
