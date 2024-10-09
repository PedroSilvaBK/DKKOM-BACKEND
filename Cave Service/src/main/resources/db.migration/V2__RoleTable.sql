CREATE TABLE cave_roles (
    id BINARY(16) NOT NULL,
    name varchar(30) NOT NULL,
    cave_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (cave_id) REFERENCES caves(id)
);

CREATE TABLE cave_role_permissions (
    cave_role_id BINARY(16) NOT NULL,
    permission int not null,
    PRIMARY KEY (cave_role_id),
    UNIQUE (cave_role_id),
    FOREIGN KEY (cave_role_id) REFERENCES cave_roles(id)
);
