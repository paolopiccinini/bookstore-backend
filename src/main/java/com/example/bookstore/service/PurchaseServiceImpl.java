package com.example.bookstore.service;

import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Customer;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CustomerRepository;
import lombok.AllArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@AllArgsConstructor
public class PurchaseServiceImpl implements PurchaseService{
    
    private final BookRepository bookRepo;
    
    private final CustomerRepository customerRepo;

    @Transactional
    public double calculatePrice(List<String> isbns) {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        var books = bookRepo.findAllByIsbnIn(isbns);
        var customer = customerRepo.findById(username).orElse(new Customer(username));

        double total = books.stream().mapToDouble(book -> book.calculatePrice(books.size())).sum();

        
        customer.setUsername(username);
        customer.addLoyaltyPoint(books.size());

        if (customer.getLoyaltyPoints() >= 10) {
            // remove the min price to increase revenue
            // I removed the calculated price not the base one
            // shall we reset the points if no discount is applied?
            total -= books.stream()
                .filter(Book::isDiscountableWithLoyalty)
                .mapToDouble(book -> book.calculatePrice(books.size()))
                .min()
                .orElse(0.0);
            customer.setLoyaltyPoints(0);
        }

        customerRepo.save(customer);
        return total;
    }

    @Transactional(readOnly = true)
    public int getLoyaltyPoints() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepo.findById(username).map(Customer::getLoyaltyPoints).orElse(0);
    }
}

