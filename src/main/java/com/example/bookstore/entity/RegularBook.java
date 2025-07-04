package com.example.bookstore.entity;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@DiscriminatorValue("REGULAR")
public class RegularBook extends Book {
    
    public RegularBook(BookDto request) {
        super(request);
    }

    @Override
    public double calculatePrice(int numOfBooks) {
        var discount = 1.0;
        if(numOfBooks >= 3) {
            discount = 0.9;
        }
        return getBasePrice() * discount;
    }

    @Override
    public boolean isDiscountableWithLoyalty() {
        return true;
    }

    @Override
    public BookType getType() {
        return BookType.REGULAR;
    }
}
