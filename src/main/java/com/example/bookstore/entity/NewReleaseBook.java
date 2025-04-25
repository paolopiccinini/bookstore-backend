package com.example.bookstore.entity;

import com.example.bookstore.dto.BookRequest;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@DiscriminatorValue("NEW_RELEASE")
public class NewReleaseBook extends Book{

    public NewReleaseBook(BookRequest request) {
        super(request);
    }

    @Override
    public double calculatePrice(int numOfBooks) {
        return getBasePrice();
    }

    @Override
    public boolean isDiscountableWithLoyalty() {
        return false;
    }
    
}
