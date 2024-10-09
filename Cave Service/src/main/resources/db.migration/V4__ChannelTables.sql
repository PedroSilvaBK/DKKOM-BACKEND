CREATE TABLE channel
(
    id   BINARY(16) NOT NULL,
    cave_id BINARY(16) NOT NULL,
    name VARCHAR(50),
    PRIMARY KEY (id),
    FOREIGN KEY (cave_id) REFERENCES caves(id)
);

CREATE TABLE voice_channels
(
    id   BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES channel(id)
);

CREATE TABLE chat_channels
(
    id   BINARY(16) NOT NULL,
    description varchar(255) NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (id) REFERENCES channel(id)
);

CREATE TABLE channel_roles
(
    id         BINARY(16) NOT NULL,
    entity_id    BINARY(16) NOT NULL,
    entity_type int NOT NULL,
    channel_id BINARY(16) NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (channel_id) REFERENCES channel(id)
);


CREATE TABLE channel_role_permissions (
    channel_role_id BINARY(16) NOT NULL,
    permission int NOT NULL,
    PRIMARY KEY (channel_role_id, permission),
    FOREIGN KEY (channel_role_id) REFERENCES channel_roles(id)
)