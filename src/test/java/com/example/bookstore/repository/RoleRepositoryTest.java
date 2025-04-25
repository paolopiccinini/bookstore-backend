package com.example.bookstore.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.stream.Stream;

@ActiveProfiles("test")
@DataJpaTest
class RoleRepositoryTest {
    @Autowired
    private RoleRepository roleRepository;

    @ParameterizedTest(name = "{0} found")
    @MethodSource("validProvider")
    void findByNameOk(String role) {
        var result = roleRepository.findByName(role);

        Assertions.assertTrue(result.isPresent());
    }

    @ParameterizedTest(name = "{0} not found")
    @MethodSource("invalidProvider")
    void findByNameKo(String role) {
        var result = roleRepository.findByName(role);

        Assertions.assertFalse(result.isPresent());
    }

    private static Stream<Arguments> validProvider() {
        return Stream.of(
                "ROLE_ADMIN",
                "ROLE_CUSTOMER"
        ).map(Arguments::of);
    }

    private static Stream<Arguments> invalidProvider() {
        return Stream.of(
                "ADMIN",
                "CONSUMER"
        ).map(Arguments::of);
    }
}
