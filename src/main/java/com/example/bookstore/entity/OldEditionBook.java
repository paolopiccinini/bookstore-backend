package com.example.bookstore.entity;

import com.example.bookstore.dto.BookRequest;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@DiscriminatorValue("OLD_EDITION")
public class OldEditionBook extends Book {
    
    public OldEditionBook(BookRequest request) {
        super(request);
    }

    //

    /**
     * I've not understand if u want this calculation or
     * public double getDiscountedPrice(int numOfBooks) {
     *         double baseDiscounted = getBasePrice() * 0.8; // 20% off
     *         if (numOfBooks >= 3) {
     *             return baseDiscounted * 0.95; // Extra 5% off
     *         }
     *         return baseDiscounted;
     *     }
     */
    @Override
    public double calculatePrice(int numOfBooks) {
        var discount = 0.80;
        if(numOfBooks >= 3) {
            discount = 0.75;
        }
        return getBasePrice() * discount;
    }

    @Override
    public boolean isDiscountableWithLoyalty() {
        return true;
    }
}
