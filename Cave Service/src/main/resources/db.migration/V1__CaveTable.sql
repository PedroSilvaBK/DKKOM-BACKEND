CREATE TABLE caves
(
    id   BINARY(16) NOT NULL,
    name varchar(30) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (id)
);