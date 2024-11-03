CREATE TABLE members (
    id BINARY(16) NOT NULL,
    user_id BINARY(16) NOT NULL,
    cave_id BINARY(16) NOT NULL,
    username varchar(50) NOT NULL,
    nickname varchar(50) NULL,
    joined_at datetime NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (user_id),
    FOREIGN KEY (cave_id) REFERENCES caves(id)
);

CREATE TABLE members_roles (
    member_id BINARY(16) NOT NULL,
    role_id BINARY(16) NOT NULL,
    PRIMARY KEY (member_id, role_id),
    FOREIGN KEY (member_id) references members(id),
    FOREIGN KEY (role_id) references cave_roles(id)
);