package com.example.bookstore.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Set;
import java.util.stream.Stream;

@ActiveProfiles("test")
@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    @ParameterizedTest(name = "{0} found")
    @MethodSource("validProvider")
    void findAllByIsbnInOk(String isbn) {
        var result = bookRepository.findAllByIsbnIn(Set.of(isbn));
        Assertions.assertEquals(1, result.size());
        Assertions.assertEquals(isbn, result.iterator().next().getIsbn());
    }

    @ParameterizedTest(name = "{0} found")
    @MethodSource("validProvider")
    void findByIsbnOk(String isbn) {
        var result = bookRepository.findByIsbn(isbn);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(isbn, result.get().getIsbn());
    }

    @ParameterizedTest(name = "{0} not found")
    @MethodSource("invalidProvider")
    void findAllByIsbnInKo(String isbn) {
        var result = bookRepository.findAllByIsbnIn(Set.of(isbn));
        Assertions.assertTrue(result.isEmpty());
    }

    @ParameterizedTest(name = "{0} not found")
    @MethodSource("invalidProvider")
    void findByIsbnKo(String isbn) {
        var result = bookRepository.findByIsbn(isbn);
        Assertions.assertTrue(result.isEmpty());
    }

    private static Stream<Arguments> validProvider() {
        return Stream.of(
                "ISBN-001",
                "ISBN-002",
                "ISBN-003",
                "ISBN-004",
                "ISBN-005",
                "ISBN-006",
                "ISBN-007",
                "ISBN-008",
                "ISBN-009",
                "ISBN-010",
                "ISBN-011",
                "ISBN-012"
        ).map(Arguments::of);
    }

    private static Stream<Arguments> invalidProvider() {
        return Stream.of(
                "001",
                "002",
                "003",
                "004",
                "005",
                "006",
                "007",
                "008",
                "009",
                "010",
                "011",
                "012"
        ).map(Arguments::of);
    }
}
