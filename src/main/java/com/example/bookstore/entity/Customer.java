package com.example.bookstore.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Entity
@Data
public class Customer {
    @Id
    private String username;
    private int loyaltyPoints;

    public Customer(String username) {
        this.username = username;
    }

    public void addLoyaltyPoint(int delta) {
        loyaltyPoints += delta;
    }
}
