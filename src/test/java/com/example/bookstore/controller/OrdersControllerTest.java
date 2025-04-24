package com.example.bookstore.controller;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.example.bookstore.service.PurchaseService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.example.bookstore.service.UserDetailsServiceImpl;
import com.example.bookstore.util.Constants;
import com.example.bookstore.util.JwtUtil;

@WebMvcTest(OrdersController.class)
@ActiveProfiles("test")
// I've not understood why @WithMockUser is not working here
// I set a breakpoint in the jwt filter and is not passing from there
// also without the below annotation
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
        when(purchaseService.calculatePrice(anyList())).thenReturn(19.0);

        mockMvc.perform(
                        post("/orders")
                        .contentType(Constants.VERSION_1_HEADER)
                        .param("isbns", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"))
                    .andExpect(status().isOk())
                    .andExpect(content().string("19.0"));
    }

    @Test
    @DisplayName("Given the user by 10 book WHEN buyBooks THEN the total price is calculated")
    void getLoyaltyPoints() throws Exception {
        when(purchaseService.getLoyaltyPoints()).thenReturn(9);

        mockMvc.perform(
                        get("/orders/points")
                        .contentType(Constants.VERSION_1_HEADER))
                    .andExpect(status().isOk())
                    .andExpect(content().string("9"));
    }

}
