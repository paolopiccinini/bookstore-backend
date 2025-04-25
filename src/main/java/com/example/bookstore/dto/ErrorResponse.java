package com.example.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class ErrorResponse {
    @Schema(description = "General error message", example = "Internal server error")
    private final String message;
    @Schema(description = "Specific error messages", example = "isbn can't be blank")
    private final List<String> errors;
}
