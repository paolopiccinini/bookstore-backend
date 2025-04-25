package com.example.bookstore.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("GIVEN a valid username WHEN findByUsername THEN the user is found")
    void findByUsernameOk() {
        var result = userRepository.findByUsername("username");
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals("username", result.get().getUsername());
        Assertions.assertEquals("$2a$10$hcOucV6DY/JI6HQfkhEaxep4n/xZFhgh0n5bHfFK40Zf9Ni9.Ltue", result.get().getPassword());
    }

    @Test
    @DisplayName("GIVEN a invalid username WHEN findByUsername THEN the user is not found")
    void findByUsernameKo() {
        var result = userRepository.findByUsername("notExisting");
        Assertions.assertFalse(result.isPresent());
    }
}
