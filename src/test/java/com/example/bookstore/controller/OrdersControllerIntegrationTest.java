package com.example.bookstore.controller;

import com.example.bookstore.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class OrdersControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Test
    @DisplayName("Given the user by 12 book WHEN buyBooks THEN the total price is calculated")
    @WithMockUser(username = "username", roles = "CUSTOMER")
    void buyBooks12OK() throws Exception {
        mockMvc.perform(
                        post("/orders")
                                .contentType(Constants.VERSION_1_HEADER)
                                .param("isbns", "ISBN-001", "ISBN-002", "ISBN-003", "ISBN-004", "ISBN-005", "ISBN-006", "ISBN-007", "ISBN-008", "ISBN-009", "ISBN-010", "ISBN-011", "ISBN-012"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(463.4));
    }

    @Test
    @DisplayName("Given the user by 3 book WHEN buyBooks THEN the total price is calculated")
    @WithMockUser(username = "username", roles = "CUSTOMER")
    void buyBooks3OK() throws Exception {
        mockMvc.perform(
                        post("/orders")
                                .contentType(Constants.VERSION_1_HEADER)
                                .param("isbns", "ISBN-001", "ISBN-002", "ISBN-003"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(95.0));
    }

    @Test
    @DisplayName("Given the user by 3 book WHEN buyBooks THEN the total price is calculated")
    @WithMockUser(username = "username", roles = "CUSTOMER")
    void buyBooks2OK() throws Exception {
        mockMvc.perform(
                        post("/orders")
                                .contentType(Constants.VERSION_1_HEADER)
                                .param("isbns", "ISBN-001", "ISBN-002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(40.0));
    }

    @Test
    @DisplayName("Given the user wants to know his loyalty points WHEN getLoyaltyPoints THEN the total points are retrieved")
    @WithMockUser(username = "username", roles = "CUSTOMER")
    void getLoyaltyPointsOk() throws Exception {
        mockMvc.perform(
                        get("/orders/points")
                                .contentType(Constants.VERSION_1_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().string("0"));
    }
}
