package com.example.bookstore.controller;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.dto.BookResponse;
import com.example.bookstore.dto.BookType;
import com.example.bookstore.dto.ErrorResponse;
import com.example.bookstore.service.BookService;
import com.example.bookstore.util.Constants;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Books", description = "Books management APIs")
@RestController
@RequestMapping("/books")
@AllArgsConstructor
public class BooksController {
    
    private final BookService bookService;

    @Operation(
            summary = "Creates a new Book",
            description = "Creates a new Book"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201",  description = "Book created",content = { @Content(schema = @Schema(implementation = BookResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "404", description = "Should never happen", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "415", description = "Invalid media type", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
    })
    @PostMapping
    @Secured("ROLE_ADMIN")
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest book) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(book));
    }

    @Operation(
            summary = "Update a Book",
            description = "Update a Book given the isbn"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated", content = { @Content(schema = @Schema(implementation = BookResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "404", description = "Book not found", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "415", description = "Invalid media type", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
    })
    @PutMapping
    @Secured("ROLE_ADMIN")
    public BookResponse updateBook(@Valid @RequestBody BookRequest book) {
        return bookService.updateBook(book);
    }

    @Operation(
            summary = "Get a Book",
            description = "Get a Book given the isbn"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book found", content = { @Content(schema = @Schema(implementation = BookResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "404", description = "Book not found", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "415", description = "Invalid media type", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
    })
    @GetMapping("/{isbn}")
    public BookResponse getBook(@PathVariable("isbn") String isbn) {
        return bookService.getBook(isbn);
    }

    @Operation(
            summary = "Delete a Book",
            description = "Delete a book given the isbn"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book deleted", content = { @Content(schema = @Schema(implementation = Void.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "404", description = "Book not found", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "415", description = "Invalid media type", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
    })
    @DeleteMapping("/{isbn}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> deleteBook(@PathVariable("isbn") String isbn) {
        bookService.deleteBook(isbn);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "Get Books",
            description = "Get a list of Books given: title, author, bookType, minPrice, maxPrice, page, size"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books retrieved", content = { @Content(array = @ArraySchema(schema = @Schema(implementation = BookResponse.class)), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "400", description = "Bad request", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "404", description = "Should never happen", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "415", description = "Invalid media type", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
            @ApiResponse(responseCode = "500", description = "Internal server error", content = { @Content(schema = @Schema(implementation = ErrorResponse.class), mediaType = Constants.VERSION_1_HEADER) }),
    })
    @GetMapping
    public Page<BookResponse> getBooks(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String author,
        @RequestParam(required = false) BookType bookType,
        @RequestParam(required = false) Double minPrice,
        @RequestParam(required = false) Double maxPrice,
        @PageableDefault(size = 10) Pageable pageable
    ) {
        return bookService.getFilteredBooks(title, author, bookType, minPrice, maxPrice, pageable);
    }
}
