package com.example.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode
public class CalculatePriceResponse {
    @Schema(description = "Books in the order")
    private Set<CalculatePriceDto> books;
    @Schema(description = "Total price of the order", example = "10.1")
    private double total;
    @Schema(description = "Total saved from the order", example = "10.1")
    private double saved;
    @Schema(description = "Isbn not found in the store", example = "10.1")
    private Set<String> isbnsNotFound;
    @Schema(description = "loyalty points of the user", example = "9")
    private int loyaltyPoints;
}
