package com.example.bookstore.controller;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookType;
import com.example.bookstore.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class BookControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Test
    @DisplayName("GIVEN a valid BookRequest WHEN createBook THEN a valid BookResponse is returned")
    @WithMockUser(roles = {"ADMIN"})
    void createBookCreated() throws Exception {
        var bookRequest = new BookDto();
        bookRequest.setBookType(BookType.NEW_RELEASE);
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("1");
        bookRequest.setTitle("title");
        bookRequest.setBasePrice(15.4);

        mockMvc.perform(
                        post("/books")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(jacksonObjectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.author").value("Author"))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.isbn").value("1"))
                .andExpect(jsonPath("$.basePrice").value(15.4));
    }

    @Test
    @DisplayName("GIVEN a user with invalid role WHEN createBook THEN a forbidden is returned")
    @WithMockUser(roles = {"CUSTOMER"})
    void createBookForbidden() throws Exception {
        var bookRequest = new BookDto();
        bookRequest.setBookType(BookType.NEW_RELEASE);
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("1");
        bookRequest.setTitle("title");
        bookRequest.setBasePrice(15.4);

        mockMvc.perform(
                        post("/books")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(jacksonObjectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GIVEN a valid BookRequest WHEN updateBook THEN a valid BookResponse is returned")
    @WithMockUser(roles = {"ADMIN"})
    void updateBookOk() throws Exception {
        var bookRequest = new BookDto();
        bookRequest.setBookType(BookType.NEW_RELEASE);
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("ISBN-001");
        bookRequest.setTitle("title");
        bookRequest.setBasePrice(15.4);

        mockMvc.perform(
                        put("/books")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(jacksonObjectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.author").value("Author"))
                .andExpect(jsonPath("$.title").value("title"))
                .andExpect(jsonPath("$.isbn").value("ISBN-001"))
                .andExpect(jsonPath("$.basePrice").value(15.4));
    }

    @Test
    @DisplayName("GIVEN a invalid isbn WHEN updateBook THEN a 404 is returned")
    @WithMockUser(roles = {"ADMIN"})
    void updateBookNotFound() throws Exception {
        var bookRequest = new BookDto();
        bookRequest.setBookType(BookType.NEW_RELEASE);
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("-001");
        bookRequest.setTitle("title");
        bookRequest.setBasePrice(15.4);

        mockMvc.perform(
                        put("/books")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(jacksonObjectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GIVEN the user has invalid role WHEN updateBook THEN a 403 is returned")
    @WithMockUser(roles = {"CUSTOMER"})
    void updateBookForbidden() throws Exception {
        var bookRequest = new BookDto();
        bookRequest.setBookType(BookType.NEW_RELEASE);
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("-001");
        bookRequest.setTitle("title");
        bookRequest.setBasePrice(15.4);

        mockMvc.perform(
                        put("/books")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(jacksonObjectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GIVEN a valid isbn WHEN deleteBook THEN the book is deleted")
    @WithMockUser(roles = {"ADMIN"})
    void deleteBookNoContent() throws Exception {

        mockMvc.perform(
                        delete("/books/ISBN-001")
                                .contentType(Constants.VERSION_1_HEADER))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GIVEN a valid request WHEN getBooks THEN a Page of BookResponse is returned")
    @WithMockUser(roles = {"CUSTOMER"})
    void getBooksOk() throws Exception {
        mockMvc.perform(
                        get("/books")
                                .contentType(Constants.VERSION_1_HEADER)
                                .param("author", "author")
                                .param("title", "Effective Java")
                                .param("bookType", "OLD_EDITION")
                                .param("minPrice", "1.0")
                                .param("maxPrice", "60.0")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].isbn").value("ISBN-002"))
                .andExpect(jsonPath("$.content[0].title").value("Effective Java"))
                .andExpect(jsonPath("$.content[0].author").value("author2"))
                .andExpect(jsonPath("$.content[0].basePrice").value(50.0))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1))
        ;
    }

    @Test
    @DisplayName("GIVEN a valid request WHEN getBooks THEN a Page of BookResponse is returned")
    @WithAnonymousUser
    void getBooksForbidden() throws Exception {
        mockMvc.perform(
                        get("/books")
                                .contentType(Constants.VERSION_1_HEADER)
                                .param("author", "author")
                                .param("title", "Effective Java")
                                .param("bookType", "OLD_EDITION")
                                .param("minPrice", "1.0")
                                .param("maxPrice", "60.0")
                )
                .andExpect(status().isForbidden());
    }

}
