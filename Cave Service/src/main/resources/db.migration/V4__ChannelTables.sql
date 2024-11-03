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
    entity_name varchar(50) NOT NULL ,
    channel_id BINARY(16) NOT NULL,
    allow int NOT NULL,
    deny int NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (id),
    FOREIGN KEY (channel_id) REFERENCES channel(id)
);