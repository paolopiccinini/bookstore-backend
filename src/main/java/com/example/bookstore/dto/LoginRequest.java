package com.example.bookstore.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @Schema(description = "username", example = "mario rossi")
    @NotBlank(message = "username can't be blank")
    private String username;
    @Schema(description = "password", example = "super_secret")
    @NotBlank(message = "password can't be blank")
    private String password;
}
