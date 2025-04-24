package com.example.bookstore.service;

import java.util.List;

public interface PurchaseService {

    double calculatePrice(List<String> isbns);
    int getLoyaltyPoints();
}
