CREATE TABLE cave_invites (
    id BINARY(16) NOT NULL,
    cave_id BINARY(16) NOT NULL,
    expiration_date DATETIME NOT NULL,
    max_uses int not null,
    invite_uses int not null,

    PRIMARY KEY (id),
    FOREIGN KEY (cave_id) REFERENCES caves(id)
)