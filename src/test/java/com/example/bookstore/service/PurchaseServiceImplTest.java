package com.example.bookstore.service;

import com.example.bookstore.entity.Customer;
import com.example.bookstore.entity.NewReleaseBook;
import com.example.bookstore.entity.OldEditionBook;
import com.example.bookstore.entity.RegularBook;
import com.example.bookstore.repository.BookRepository;
import com.example.bookstore.repository.CustomerRepository;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.User;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class PurchaseServiceImplTest {

    @Mock
    private BookRepository bookRepo;
    @Mock
    private  CustomerRepository customerRepo;

    @InjectMocks
    private PurchaseServiceImpl sut;

    private MockedStatic<SecurityContextHolder> mockedStaticClass;

    @BeforeEach
    public void setUp()
    {
        mockedStaticClass = mockStatic(SecurityContextHolder.class);
        var securityContext = new SecurityContextImpl();
        var user = new User("username", "password", Set.of(new SimpleGrantedAuthority("ADMIN")));
        var authentication = new UsernamePasswordAuthenticationToken( user,null, user.getAuthorities());
        securityContext.setAuthentication(authentication);
        mockedStaticClass.when(SecurityContextHolder::getContext).thenReturn(securityContext);
    }

    @AfterEach
    public void tearDown() throws Exception
    {
        mockedStaticClass.close();
    }

    // 10 + 80 * 0.8 = 74
    @Test
    @DisplayName("Given a list of two isbn old new WHEN calculatePrice THEN the prices for the order are calculated with only old discount")
    void calculatePriceNoLoyaltyOldNewBook() {
        var newRelease = new NewReleaseBook();
        newRelease.setBasePrice(10.0);
        var oldRelease = new OldEditionBook();
        oldRelease.setBasePrice(80.0);
        when(bookRepo.findAllByIsbnIn(anyList())).thenReturn(List.of(newRelease, oldRelease));
        when(customerRepo.findById(anyString())).thenReturn(Optional.empty());

        var result = sut.calculatePrice(List.of());
        Assertions.assertEquals(74.0, result);
        mockedStaticClass.verify(SecurityContextHolder::getContext);
    }

    // 10 + 80 * 0.75 + 60 * 0.9 = 124
    @Test
    @DisplayName("Given a list of three isbn WHEN calculatePrice THEN the prices for the order are calculated with old regular discount")
    void calculatePriceNoLoyaltyThreeBooks() {
        var newRelease = new NewReleaseBook();
        newRelease.setBasePrice(10.0);
        var oldRelease = new OldEditionBook();
        oldRelease.setBasePrice(80.0);
        var regularRelease = new RegularBook();
        regularRelease.setBasePrice(60.0);
        when(bookRepo.findAllByIsbnIn(anyList())).thenReturn(List.of(newRelease, oldRelease, regularRelease));
        when(customerRepo.findById(anyString())).thenReturn(Optional.empty());

        var result = sut.calculatePrice(List.of());
        Assertions.assertEquals(124.0, result);
        mockedStaticClass.verify(SecurityContextHolder::getContext);
    }

    // 10 + 80 * 0.75 = 70
    @Test
    @DisplayName("Given a list of three isbn and 10 loyalty points WHEN calculatePrice THEN the prices for the order are calculated with min removed from the price and discount")
    void calculatePriceLoyaltyThreeBooks() {
        var newRelease = new NewReleaseBook();
        newRelease.setBasePrice(10.0);
        var oldRelease = new OldEditionBook();
        oldRelease.setBasePrice(80.0);
        var regularRelease = new RegularBook();
        regularRelease.setBasePrice(60.0);
        when(bookRepo.findAllByIsbnIn(anyList())).thenReturn(List.of(newRelease, oldRelease, regularRelease));
        var customer = new Customer("paolo");
        customer.setLoyaltyPoints(10);
        when(customerRepo.findById(anyString())).thenReturn(Optional.of(customer));

        var result = sut.calculatePrice(List.of());
        Assertions.assertEquals(70.0, result);
        mockedStaticClass.verify(SecurityContextHolder::getContext);
    }


}
