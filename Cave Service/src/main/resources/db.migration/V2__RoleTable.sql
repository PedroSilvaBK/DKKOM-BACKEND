CREATE TABLE cave_roles (
    id BINARY(16) NOT NULL,
    name varchar(30) NOT NULL,
    cave_id BINARY(16) NOT NULL,
    permissions int NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (cave_id) REFERENCES caves(id)
);