package com.example.bookstore.dto;

import com.example.bookstore.entity.Book;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class CalculatePriceDto extends BookDto {
    @Schema(description = "Calculated price of the book", example = "10.1")
    private double calculatedPrice;

    public CalculatePriceDto (Book book, int numOfBooks) {
        super(book);
        calculatedPrice = book.calculatePrice(numOfBooks);
    }
}
