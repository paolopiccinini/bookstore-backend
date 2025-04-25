package com.example.bookstore.controller;

import com.example.bookstore.dto.ErrorResponse;
import com.example.bookstore.dto.JwtToken;
import com.example.bookstore.dto.LoginRequest;
import com.example.bookstore.dto.RegisterRequest;
import com.example.bookstore.util.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

@Disabled
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private ObjectMapper jacksonObjectMapper;

    private TestRestTemplate restTemplate;
    private HttpHeaders headers;

    @BeforeEach
    public void setUp() {
        restTemplate = new TestRestTemplate();
        headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.valueOf(Constants.VERSION_1_HEADER)));
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    @DisplayName("GIVEN a Valid RegisterRequest WHEN register THEN a user is created")
    void registerCreated() throws Exception {
        var registerRequest = new RegisterRequest();
        registerRequest.setRole(com.example.bookstore.dto.Role.ADMIN);
        registerRequest.setPassword("password");
        registerRequest.setUsername("newuser");

        HttpEntity<String> entity = new HttpEntity<>(jacksonObjectMapper.writeValueAsString(registerRequest), headers);

        ResponseEntity<Void> response = restTemplate.exchange(createURLWithPort("/auth/register"), HttpMethod.POST, entity,
                Void.class);
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    @Test
    @DisplayName("GIVEN a invalid RegisterRequest WHEN register THEN a bad request is returned")
    void registerBadRequest() throws Exception {
        var registerRequest = new RegisterRequest();
        registerRequest.setRole(com.example.bookstore.dto.Role.ADMIN);
        registerRequest.setPassword("password");
        registerRequest.setUsername("username");

        HttpEntity<String> entity = new HttpEntity<>(jacksonObjectMapper.writeValueAsString(registerRequest), headers);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(createURLWithPort("/auth/register"), HttpMethod.POST, entity,
                ErrorResponse.class);
        Assertions.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Assertions.assertEquals("User alredy present: username", response.getBody().getMessage());
    }

    @Test
    @DisplayName("GIVEN a Valid LoginRequest WHEN login THEN a jwt is returned")
    public void loginOk() throws Exception {
        var loginRequest = new LoginRequest();
        loginRequest.setPassword("password");
        loginRequest.setUsername("username");

        HttpEntity<String> entity = new HttpEntity<>(jacksonObjectMapper.writeValueAsString(loginRequest), headers);

        ResponseEntity<JwtToken> response = restTemplate.exchange(createURLWithPort("/auth/login"), HttpMethod.POST, entity,
                JwtToken.class);
        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody().getToken());
    }

    @Test
    @DisplayName("GIVEN a invalid LoginRequest WHEN login THEN Unauthorized is returned")
    public void loginUnauthorized() throws Exception {
        var loginRequest = new LoginRequest();
        loginRequest.setPassword("bad");
        loginRequest.setUsername("username");

        HttpEntity<String> entity = new HttpEntity<>(jacksonObjectMapper.writeValueAsString(loginRequest), headers);

        ResponseEntity<ErrorResponse> response = restTemplate.exchange(createURLWithPort("/auth/login"), HttpMethod.POST, entity,
                ErrorResponse.class);
        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        Assertions.assertEquals("Bad credentials", response.getBody().getMessage());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}
