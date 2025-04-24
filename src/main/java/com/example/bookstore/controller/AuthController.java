package com.example.bookstore.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.bookstore.dto.ErrorResponse;
import com.example.bookstore.dto.JwtToken;
import com.example.bookstore.dto.LoginRequest;
import com.example.bookstore.dto.RegisterRequest;
import com.example.bookstore.entity.User;
import com.example.bookstore.exception.RoleNotFoundException;
import com.example.bookstore.exception.UserAlreadyPresentException;
import com.example.bookstore.repository.RoleRepository;
import com.example.bookstore.repository.UserRepository;
import com.example.bookstore.util.Constants;
import com.example.bookstore.util.JwtUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@Tag(name = "Authentications", description = "Authentication management APIs")
@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final UserRepository userRepo;
    
    private final RoleRepository roleRepo;

    private final PasswordEncoder passwordEncoder;

    private final JwtUtil jwtUtil;

    @Operation(
            summary = "Register the users",
            description = "Register the user with username, password and role"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created", content = { @Content(schema = @Schema(implementation = Void.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "404", description = "Should never happen", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "415", description = "Invalid media type", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
    })
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid @RequestBody RegisterRequest request) {
        userRepo.findByUsername(request.getUsername()).ifPresent(_ -> {
                throw new UserAlreadyPresentException(request.getUsername());
        });;

        var user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));

        var role = roleRepo.findByName("ROLE_" + request.getRole().name())
            .orElseThrow(() -> new RoleNotFoundException(request.getRole().name()));

        user.getRoles().add(role);
        userRepo.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(
            summary = "Login the users",
            description = "Login the user given username and password returns a jwt token"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Token generated", content = { @Content(schema = @Schema(implementation = JwtToken.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "400", description = "Invalid credentials", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "404", description = "Should never happen", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "415", description = "Invalid media type", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
    })
    @PostMapping("/login")
    public JwtToken login(@Valid @RequestBody LoginRequest user) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

        String token = jwtUtil.generateToken(user.getUsername());
        return new JwtToken(token);
    }
}
