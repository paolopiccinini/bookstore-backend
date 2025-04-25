package com.example.bookstore.filter;

import com.example.bookstore.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

    @Mock
    private JwtUtil jwtUtil;
    @Mock
    private UserDetailsService userDetailsService;
    @InjectMocks
    private JwtAuthenticationFilter sut;

    @BeforeEach
    public void setUp() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    @Test
    @DisplayName("GIVEN a invalid token WHEN doFilterInternal THEN the authentication is null in SecurityContextHolder")
    void doFilterInternalUserInvalidTOken() throws ServletException, IOException {
        var bearerToken = "Bearer 1234";
        var token = "1234";
        var username = "username";
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(eq("Authorization"))).thenReturn(bearerToken);
        when(jwtUtil.validateToken(eq(token))).thenReturn(false);

        sut.doFilterInternal(request, mock(HttpServletResponse.class), mock(FilterChain.class));

        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("GIVEN a no token WHEN doFilterInternal THEN the authentication is null in SecurityContextHolder")
    void doFilterInternalUserNoTOken() throws ServletException, IOException {
        var request = mock(HttpServletRequest.class);

        sut.doFilterInternal(request, mock(HttpServletResponse.class), mock(FilterChain.class));

        Assertions.assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    @DisplayName("GIVEN a valid token WHEN doFilterInternal THEN the authentication is set in SecurityContextHolder")
    void doFilterInternalUserAuthenticated() throws ServletException, IOException {
        var bearerToken = "Bearer 1234";
        var token = "1234";
        var username = "username";
        var request = mock(HttpServletRequest.class);
        when(request.getHeader(eq("Authorization"))).thenReturn(bearerToken);
        when(jwtUtil.validateToken(eq(token))).thenReturn(true);
        when(jwtUtil.extractUsername(eq(token))).thenReturn(username);
        var user = new User(username, "password", Set.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        when(userDetailsService.loadUserByUsername(eq(username))).thenReturn(user);
        var expected = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

        sut.doFilterInternal(request, mock(HttpServletResponse.class), mock(FilterChain.class));

        Assertions.assertEquals(expected.getPrincipal(), SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Assertions.assertEquals(expected.getAuthorities(), SecurityContextHolder.getContext().getAuthentication().getAuthorities());
    }
}
