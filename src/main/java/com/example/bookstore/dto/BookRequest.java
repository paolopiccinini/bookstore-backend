package com.example.bookstore.dto;

import com.example.bookstore.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
@EqualsAndHashCode(callSuper=true)
public class BookRequest extends BookResponse {
    @NotNull(message = "bookType can't be null valid values: NEW_RELEASE, REGULAR, OLD_EDITION")
    @Schema(description = "type of the book", example = "NEW_RELEASE|REGULAR|OLD_EDITION")
    BookType bookType;

    public Book convertToBook() {
        return bookType.createBookInstance(this);
    }
}
