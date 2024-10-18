CREATE TABLE users
(
    id   BINARY(16) NOT NULL,
    name varchar(100) NOT NULL,
    username varchar(100) NOT NULL,
    email varchar(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (id),
    UNIQUE (username)
);