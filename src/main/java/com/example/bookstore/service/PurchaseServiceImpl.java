package com.example.bookstore.service;

import com.example.bookstore.dto.CalculatePriceDto;
import com.example.bookstore.dto.CalculatePriceResponse;
import com.example.bookstore.entity.Book;
import com.example.bookstore.entity.Customer;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CustomerRepository;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class PurchaseServiceImpl implements PurchaseService{
    
    private final BookRepository bookRepo;
    
    private final CustomerRepository customerRepo;


    @Transactional
    public CalculatePriceResponse calculatePrice(Set<String> isbns) {
        log.info("Calculating price getting logged user");
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        log.info("finding books");
        var books = bookRepo.findAllByIsbnIn(isbns);
        log.info("finding customer");
        var customer = customerRepo.findById(username).orElse(new Customer(username));
        var total = books.stream().mapToDouble(book -> book.calculatePrice(books.size())).sum();
        log.info("Calculating the total {}", total);
        customer.addLoyaltyPoint(books.size());
        log.info("Updating loyalty points {}", customer.getLoyaltyPoints());
        var atomicTotal = new AtomicReference<>(total);
        var basicTotal = books.stream().mapToDouble(Book::getBasePrice).sum();
        if (customer.getLoyaltyPoints() >= 10) {
            log.info("loyalty points >= 10 calculating discount");
            // remove the min price to increase revenue
            // I removed the calculated price not the base one
            // shall we reset the points if no discount is applied?
            books.stream()
                    .filter(Book::isDiscountableWithLoyalty)
                    .reduce((a, b) -> a.calculatePrice(books.size()) > b.calculatePrice(books.size()) ? b : a)
                    .ifPresent(book -> {
                        atomicTotal.updateAndGet(v -> v - book.calculatePrice(books.size()));
                        book.setGratis(true);
                        customer.setLoyaltyPoints(0);
                        log.info("loyalty points resetting to 0");
                    });
        }
        var result = new CalculatePriceResponse();
        result.setBooks(books.stream().map(book -> book.convertToDto(books.size())).collect(Collectors.toSet()));
        result.setTotal(atomicTotal.get());
        log.info("Resetting the total {}", atomicTotal.get());
        result.setLoyaltyPoints(customer.getLoyaltyPoints());
        result.setSaved(basicTotal - atomicTotal.get());
        var difference = new HashSet<>(isbns);
        difference.removeAll(books.stream().map(Book::getIsbn).collect(Collectors.toSet()));
        result.setIsbnsNotFound(difference);
        log.info("Updating customer");
        customerRepo.save(customer);
        return result;
    }

    @Transactional(readOnly = true)
    public int getLoyaltyPoints() {
        log.info("Getting points fo the logged user");
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return customerRepo.findById(username).map(Customer::getLoyaltyPoints).orElse(0);
    }
}

