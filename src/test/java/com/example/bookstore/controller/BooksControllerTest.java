package com.example.bookstore.controller;

import com.example.bookstore.dto.BookDto;
import com.example.bookstore.dto.BookType;
import com.example.bookstore.dto.ErrorResponse;
import com.example.bookstore.service.BookService;
import com.example.bookstore.util.Constants;
import com.example.bookstore.util.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BooksController.class)
@AutoConfigureMockMvc(addFilters = false)
class BooksControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @MockitoBean
    private BookService bookService;

    private JacksonTester<BookDto> bookDtoJacksonTester;
    private JacksonTester<ErrorResponse> errorResponseJacksonTester;

    @MockitoBean
    private JwtUtil jwtUtil;
    @MockitoBean
    private UserDetailsService userDetailsService;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
        var user = new User("user", "password", Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(userDetailsService.loadUserByUsername(eq("user"))).thenReturn(user);
    }

    @Test
    @DisplayName("GIVEN a valid BookRequest WHEN createBook THEN a valid BookResponse is returned")
    @WithMockUser
    void createBookCreated() throws Exception {
        var bookRequest = new BookDto();
        bookRequest.setBookType(BookType.NEW_RELEASE);
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("1");
        bookRequest.setTitle("title");
        bookRequest.setBasePrice(15.4);
        var bookResponse = new BookDto();
        bookResponse.setAuthor("Author");
        bookResponse.setIsbn("1");
        bookResponse.setTitle("title");
        bookResponse.setBasePrice(15.4);
        when(bookService.createBook(eq(bookRequest))).thenReturn(bookResponse);

        mockMvc.perform(
                        post("/books")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(bookDtoJacksonTester.write(bookRequest).getJson()))
                .andExpect(status().isCreated())
                .andExpect(content().string(bookDtoJacksonTester.write(bookResponse).getJson()));
    }

    @Test
    @DisplayName("GIVEN a invalid BookRequest WHEN createBook THEN bad request is returned")
    void createBookBadRequest() throws Exception {
        var bookRequest = new BookDto();
        bookRequest.setBookType(null);
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("1");
        bookRequest.setTitle("title");
        bookRequest.setBasePrice(15.4);
        var errorResponse = new ErrorResponse("Validation failed", List.of("bookType bookType can't be null valid values: NEW_RELEASE, REGULAR, OLD_EDITION"));
        mockMvc.perform(
                        post("/books")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(bookDtoJacksonTester.write(bookRequest).getJson()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorResponseJacksonTester.write(errorResponse).getJson()));
    }

    @Test
    @DisplayName("GIVEN a valid BookRequest WHEN updateBook THEN a valid BookResponse is returned")
    void updateBookOk() throws Exception {
        var bookRequest = new BookDto();
        bookRequest.setBookType(BookType.NEW_RELEASE);
        bookRequest.setAuthor("Author");
        bookRequest.setIsbn("1");
        bookRequest.setTitle("title");
        bookRequest.setBasePrice(15.4);
        var bookResponse = new BookDto();
        bookResponse.setAuthor("Author");
        bookResponse.setIsbn("1");
        bookResponse.setTitle("title");
        bookResponse.setBasePrice(15.4);
        when(bookService.updateBook(eq(bookRequest))).thenReturn(bookResponse);

        mockMvc.perform(
                        put("/books")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(bookDtoJacksonTester.write(bookRequest).getJson()))
                .andExpect(status().isOk())
                .andExpect(content().string(bookDtoJacksonTester.write(bookResponse).getJson()));
    }

    @Test
    @DisplayName("GIVEN a valid isbn WHEN updateBook THEN a valid BookResponse is returned")
    void getBookOk() throws Exception {
        var bookResponse = new BookDto();
        bookResponse.setAuthor("Author");
        bookResponse.setIsbn("1");
        bookResponse.setTitle("title");
        bookResponse.setBasePrice(15.4);
        when(bookService.getBook(eq("1234"))).thenReturn(bookResponse);

        mockMvc.perform(
                        get("/books/1234")
                                .contentType(Constants.VERSION_1_HEADER))
                .andExpect(status().isOk())
                .andExpect(content().string(bookDtoJacksonTester.write(bookResponse).getJson()));
    }

    @Test
    @DisplayName("GIVEN a valid isbn WHEN deleteBook THEN the book is deleted")
    void deleteBookNoCOntent() throws Exception {
        doNothing().when(bookService).deleteBook(eq("1234"));

        mockMvc.perform(
                        delete("/books/1234")
                                .contentType(Constants.VERSION_1_HEADER))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("GIVEN a valid request WHEN getBooks THEN a Page of BookResponse is returned")
    void getBooksOk() throws Exception {
        var bookResponse = new BookDto();
        bookResponse.setAuthor("Author");
        bookResponse.setIsbn("1");
        bookResponse.setTitle("title");
        bookResponse.setBasePrice(15.4);
        var pageable = Pageable.ofSize(10);
        var page = new PageImpl<>(List.of(bookResponse), pageable, 1);
        when(bookService.getFilteredBooks(anyString(), anyString(), any(BookType.class), anyDouble(), anyDouble(), eq(pageable))).thenReturn(page);

        mockMvc.perform(
                        get("/books")
                                .contentType(Constants.VERSION_1_HEADER)
                                .param("author", "author")
                                .param("title", "title")
                                .param("bookType", "NEW_RELEASE")
                                .param("minPrice", "1.0")
                                .param("maxPrice", "2.0")
                                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].isbn").value("1"))
                .andExpect(jsonPath("$.content[0].title").value("title"))
                .andExpect(jsonPath("$.content[0].author").value("Author"))
                .andExpect(jsonPath("$.content[0].basePrice").value(15.4))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1))
                ;
    }
}
