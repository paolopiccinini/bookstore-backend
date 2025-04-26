package com.example.bookstore.controller;

import com.example.bookstore.dto.LoginRequest;
import com.example.bookstore.dto.RegisterRequest;
import com.example.bookstore.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper jacksonObjectMapper;

    @Test
    @DisplayName("GIVEN a Valid RegisterRequest WHEN register THEN a user is created")
    void registerCreated() throws Exception {
        var registerRequest = new RegisterRequest();
        registerRequest.setRole(com.example.bookstore.dto.Role.ADMIN);
        registerRequest.setPassword("password");
        registerRequest.setUsername("newuser");

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(jacksonObjectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GIVEN a invalid RegisterRequest WHEN register THEN a bad request is returned")
    void registerBadRequest() throws Exception {
        var registerRequest = new RegisterRequest();
        registerRequest.setRole(com.example.bookstore.dto.Role.ADMIN);
        registerRequest.setPassword("password");
        registerRequest.setUsername("username");

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(jacksonObjectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("User alredy present: username"));
    }

    @Test
    @DisplayName("GIVEN a Valid LoginRequest WHEN login THEN a jwt is returned")
    public void loginOk() throws Exception {
        var loginRequest = new LoginRequest();
        loginRequest.setPassword("password");
        loginRequest.setUsername("username");

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(jacksonObjectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value(Matchers.notNullValue()));
    }

    @Test
    @DisplayName("GIVEN a invalid LoginRequest WHEN login THEN Unauthorized is returned")
    public void loginUnauthorized() throws Exception {
        var loginRequest = new LoginRequest();
        loginRequest.setPassword("bad");
        loginRequest.setUsername("username");

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(jacksonObjectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Bad credentials"));
    }

}
