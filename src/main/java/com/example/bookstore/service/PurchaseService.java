package com.example.bookstore.service;

import com.example.bookstore.dto.CalculatePriceResponse;

import java.util.Set;

public interface PurchaseService {

    CalculatePriceResponse calculatePrice(Set<String> isbns);
    int getLoyaltyPoints();
}
