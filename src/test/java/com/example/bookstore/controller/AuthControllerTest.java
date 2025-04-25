package com.example.bookstore.controller;

import com.example.bookstore.dto.ErrorResponse;
import com.example.bookstore.dto.JwtToken;
import com.example.bookstore.dto.LoginRequest;
import com.example.bookstore.dto.RegisterRequest;
import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.RoleRepository;
import com.example.bookstore.repository.UserRepository;
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
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
// I've not understood why @WithMockUser is not working here
// I set a breakpoint in the jwt filter and is not passing from there
// also without the below annotation
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationManager authenticationManager;
    
    @MockitoBean
    private UserRepository userRepo;
    
    @MockitoBean
    private RoleRepository roleRepo;
    
    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private UserDetailsService userDetailsService;
    
    @MockitoBean
    private JwtUtil jwtUtil;

    private JacksonTester<JwtToken> jwtTokenJacksonTester;
    private JacksonTester<RegisterRequest> registerRequestJacksonTester;
    private JacksonTester<LoginRequest> loginRequestJacksonTester;
    private JacksonTester<ErrorResponse> errorResponseJacksonTester;

    @BeforeEach
    public void setup() {
        JacksonTester.initFields(this, new ObjectMapper());
    }

    @Test
    @DisplayName("GIVEN a Valid RegisterRequest WHEN register THEN a user is created")
    public void registerCreated() throws Exception {
        var registerRequest = new RegisterRequest();
        registerRequest.setRole(com.example.bookstore.dto.Role.ADMIN);
        registerRequest.setPassword("password");
        registerRequest.setUsername("username");
        when(userRepo.findByUsername(eq("username"))).thenReturn(Optional.empty());
        when(roleRepo.findByName(eq("ROLE_" + registerRequest.getRole().name()))).thenReturn(Optional.of(new Role()));

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(registerRequestJacksonTester.write(registerRequest).getJson()))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GIVEN a already existing user WHEN register THEN bad request is returned")
    public void registerUserAlreadyPresent() throws Exception {
        var registerRequest = new RegisterRequest();
        registerRequest.setRole(com.example.bookstore.dto.Role.ADMIN);
        registerRequest.setPassword("password");
        registerRequest.setUsername("username");
        when(userRepo.findByUsername(eq("username"))).thenReturn(Optional.of(new User()));
        var errorResponse = new ErrorResponse("User alredy present: username", List.of());

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(registerRequestJacksonTester.write(registerRequest).getJson()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorResponseJacksonTester.write(errorResponse).getJson()));
    }

    @Test
    @DisplayName("GIVEN the role is not found WHEN register THEN bad request is returned")
    public void registerRoleNotFound() throws Exception {
        var registerRequest = new RegisterRequest();
        registerRequest.setRole(com.example.bookstore.dto.Role.ADMIN);
        registerRequest.setPassword("password");
        registerRequest.setUsername("username");
        when(userRepo.findByUsername(eq("username"))).thenReturn(Optional.empty());
        when(roleRepo.findByName(eq("ROLE_" + registerRequest.getRole().name()))).thenReturn(Optional.empty());
        var errorResponse = new ErrorResponse("Role not found: ADMIN", List.of());

        mockMvc.perform(
                        post("/auth/register")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(registerRequestJacksonTester.write(registerRequest).getJson()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorResponseJacksonTester.write(errorResponse).getJson()));
    }

    @Test
    @DisplayName("GIVEN a Invalid RegisterRequest WHEN register THEN bad request is returned")
    public void registerBadRequest() throws Exception {
        var registerRequest = new RegisterRequest();
        registerRequest.setRole(null);
        registerRequest.setPassword("password");
        registerRequest.setUsername("username");
        var errorResponse = new ErrorResponse("Validation failed", List.of("role role can't be null, CUSTOMER or ADMIN"));
        mockMvc.perform(
                        post("/auth/register")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(registerRequestJacksonTester.write(registerRequest).getJson()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorResponseJacksonTester.write(errorResponse).getJson()));
    }

    @Test
    @DisplayName("GIVEN a Valid LoginRequest WHEN login THEN a jwt is returned")
    public void loginOk() throws Exception {
        var loginRequest = new LoginRequest();
        loginRequest.setPassword("password");
        loginRequest.setUsername("username");
        when(jwtUtil.generateToken(eq(loginRequest.getUsername()))).thenReturn("123");
        mockMvc.perform(
                        post("/auth/login")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(loginRequestJacksonTester.write(loginRequest).getJson()))
                .andExpect(status().isOk())
                .andExpect(content().string(jwtTokenJacksonTester.write(new JwtToken("123")).getJson()));
    }

    @Test
    @DisplayName("GIVEN a Bad credentials WHEN login THEN unauthorized is returned")
    public void loginUnauthorized() throws Exception {
        var loginRequest = new LoginRequest();
        loginRequest.setPassword("password");
        loginRequest.setUsername("username");
        var authentication = new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
        when(authenticationManager.authenticate(eq(authentication))).thenThrow(new BadCredentialsException("invalid"));
        var errorResponse = new ErrorResponse("invalid", List.of());

        mockMvc.perform(
                        post("/auth/login")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(loginRequestJacksonTester.write(loginRequest).getJson()))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(errorResponseJacksonTester.write(errorResponse).getJson()));
    }

    @Test
    @DisplayName("GIVEN a Invalid LoginRequest WHEN login THEN bad request is returned")
    public void loginBadRequest() throws Exception {
        var loginRequest = new LoginRequest();
        loginRequest.setPassword("password");
        var errorResponse = new ErrorResponse("Validation failed", List.of("username username can't be blank"));
        mockMvc.perform(
                        post("/auth/login")
                                .contentType(Constants.VERSION_1_HEADER)
                                .content(loginRequestJacksonTester.write(loginRequest).getJson()))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorResponseJacksonTester.write(errorResponse).getJson()));
    }
    
}
