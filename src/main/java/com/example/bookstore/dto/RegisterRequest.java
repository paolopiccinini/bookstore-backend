package com.example.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class RegisterRequest extends LoginRequest {
    @Schema(description = "Role of the user", example = "ADMIN|CUSTOMER")
    @NotNull(message = "role can't be null, CUSTOMER or ADMIN")
    private Role role;
}
