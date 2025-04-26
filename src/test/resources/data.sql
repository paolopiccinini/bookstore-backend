delete from book;
INSERT INTO book (id, title, isbn, base_price, book_type, author)
VALUES
(1000001, 'The Pragmatic Programmer', 'ISBN-001', 45.0, 'REGULAR', 'author1'),
(1000002, 'Effective Java', 'ISBN-002', 50.0, 'OLD_EDITION', 'author2'),
(1000003, 'Clean Code', 'ISBN-003', 55.0, 'REGULAR', 'author3'),
(1000004, 'Spring in Action', 'ISBN-004', 60.0, 'NEW_RELEASE', 'author4'),
(1000005, 'Domain-Driven Design', 'ISBN-005', 40.0, 'OLD_EDITION', 'author5'),
(1000006, 'Refactoring', 'ISBN-006', 48.0, 'REGULAR', 'author6'),
(1000007, 'Design Patterns', 'ISBN-007', 52.0, 'OLD_EDITION', 'author7'),
(1000008, 'Test Driven Development', 'ISBN-008', 38.0, 'REGULAR', 'author8'),
(1000009, 'Java Concurrency in Practice', 'ISBN-009', 65.0, 'NEW_RELEASE', 'author9'),
(1000010, 'Introduction to Algorithms', 'ISBN-010', 70.0, 'REGULAR', 'author10'),
(1000011, 'You Don’t Know JS', 'ISBN-011', 30.0, 'OLD_EDITION', 'author11'),
(1000012, 'Kotlin in Action', 'ISBN-012', 42.0, 'NEW_RELEASE', 'author12');

delete from user_entity;
INSERT into user_entity(id, username, password) VALUES
(100000, 'username', '$2a$10$hcOucV6DY/JI6HQfkhEaxep4n/xZFhgh0n5bHfFK40Zf9Ni9.Ltue');

delete from user_entity_roles;
INSERT into user_entity_roles (user_id, roles_id) VALUES (100000, 1);