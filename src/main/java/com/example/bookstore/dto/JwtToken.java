package com.example.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class JwtToken {
    @Schema(description = "the token to use in the Authorization Bearer:")
    private final String token;
}
