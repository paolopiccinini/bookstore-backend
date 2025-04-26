package com.example.bookstore.controller;

import com.example.bookstore.dto.CalculatePriceResponse;
import com.example.bookstore.service.PurchaseService;
import com.example.bookstore.util.Constants;
import com.example.bookstore.util.JwtUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrdersController.class)
@AutoConfigureMockMvc(addFilters = false)
class OrdersControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PurchaseService purchaseService;

    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private UserDetailsService userDetailsService;


    @Test
    @DisplayName("Given the user by 10 book WHEN buyBooks THEN the total price is calculated")
    void buyBooksOK() throws Exception {
        var response = new CalculatePriceResponse();
        response.setTotal(19.1);
        when(purchaseService.calculatePrice(anySet())).thenReturn(response);

        mockMvc.perform(
                        post("/orders")
                        .contentType(Constants.VERSION_1_HEADER)
                        .param("isbns", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.total").value(19.1));
    }

    @Test
    @DisplayName("Given the user wants to know his loyalty points WHEN getLoyaltyPoints THEN the points are returned")
    void getLoyaltyPointsOk() throws Exception {
        when(purchaseService.getLoyaltyPoints()).thenReturn(9);

        mockMvc.perform(
                        get("/orders/points")
                        .contentType(Constants.VERSION_1_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(content().string("9"));
    }

}
