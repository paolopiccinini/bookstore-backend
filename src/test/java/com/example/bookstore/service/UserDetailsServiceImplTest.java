package com.example.bookstore.service;

import com.example.bookstore.entity.Role;
import com.example.bookstore.entity.User;
import com.example.bookstore.repository.UserRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepo;
    @InjectMocks
    private UserDetailsServiceImpl sut;

    @Test
    @DisplayName("GIVEN a valid username WHEN loadUserByUsername THEN a valid user is returned")
    void loadUserByUsernameOk() {
        var username = "username";
        var user = new User();
        user.setUsername(username);
        user.setPassword("password");
        var role = new Role();
        role.setName("ROLE_ADMIN");
        user.setRoles(Set.of(role));
        when(userRepo.findByUsername(eq(username))).thenReturn(Optional.of(user));

        var result = sut.loadUserByUsername(username);
        Assertions.assertEquals(user.getUsername(), result.getUsername());
        Assertions.assertEquals(user.getPassword(), result.getPassword());
        Assertions.assertEquals(role.getName(), result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    @DisplayName("GIVEN a valid username WHEN loadUserByUsername THEN UsernameNotFoundException is thrown")
    void loadUserByUsernameUsernameNotFoundException() {
        var username = "username";
        when(userRepo.findByUsername(eq(username))).thenThrow(new UsernameNotFoundException("User not found"));

        var exception = Assertions.assertThrows(UsernameNotFoundException.class, () -> sut.loadUserByUsername(username));
        Assertions.assertEquals("User not found", exception.getMessage());
    }
}
