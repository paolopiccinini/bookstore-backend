package com.example.bookstore.controller;

import java.util.List;

import com.example.bookstore.service.PurchaseService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookstore.dto.ErrorResponse;
import com.example.bookstore.util.Constants;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Tag(name = "Orders", description = "Orders management APIs")
@RestController
@RequestMapping("/orders")
@AllArgsConstructor
public class OrdersController {

    private final PurchaseService purchaseService;

    @Operation(
            summary = "Register the users",
            description = "Register the user with username, password and role"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Price calculated", content = { @Content(schema = @Schema(implementation = Double.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "404", description = "Should never happen", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "415", description = "Invalid media type", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
    })
    @PostMapping
    public double buyBooks(@Valid @NotNull @RequestParam List<String> isbns) {
        return purchaseService.calculatePrice(isbns);
    }

    @Operation(
            summary = "Register the users",
            description = "Register the user with username, password and role"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Points retrieved", content = { @Content(schema = @Schema(implementation = Integer.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "400", description = "Should never happen", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "404", description = "Should never happen", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "415", description = "Invalid media type", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
    })
    @GetMapping("/points")
    public int getLoyaltyPoints() {
        return purchaseService.getLoyaltyPoints();
    }
}
