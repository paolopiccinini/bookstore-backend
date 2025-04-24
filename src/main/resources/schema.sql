CREATE TABLE book (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    base_price DOUBLE NOT NULL,
    book_type VARCHAR(50) NOT NULL,
    isbn VARCHAR(13) NOT NULL UNIQUE
);

CREATE TABLE role (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

CREATE TABLE user_entity (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

CREATE TABLE user_entity_roles (
    user_id BIGINT NOT NULL,
    roles_id BIGINT NOT NULL,
    PRIMARY KEY (user_id, roles_id),
    FOREIGN KEY (user_id) REFERENCES user_entity(id) ON DELETE CASCADE,
    FOREIGN KEY (roles_id) REFERENCES role(id) ON DELETE CASCADE
);

CREATE TABLE customer (
    username VARCHAR(50) PRIMARY KEY,
    loyalty_points INT NOT NULL,
    CONSTRAINT fk_customer_username FOREIGN KEY (username)
        REFERENCES user_entity(username) ON DELETE CASCADE
);

CREATE unique INDEX idx_book_isbn ON book(isbn);
create unique INDEX idx_user_username_password on user_entity(username, password);