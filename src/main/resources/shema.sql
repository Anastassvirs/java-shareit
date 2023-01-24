DROP SCHEMA public CASCADE;
CREATE SCHEMA public;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name  VARCHAR(255)                            NOT NULL,
    email VARCHAR(512)                            NOT NULL,
    CONSTRAINT pk_user PRIMARY KEY (id),
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    description  VARCHAR(512)                            NOT NULL,
    requestor_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    created      TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_request PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS items
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    name         VARCHAR(255)                            NOT NULL,
    description  VARCHAR(512)                            NOT NULL,
    is_avaliable BOOLEAN                                 NOT NULL,
    owner_id     INTEGER REFERENCES users (id) ON DELETE CASCADE,
    request_id   INTEGER REFERENCES requests (id) ON DELETE CASCADE,
    CONSTRAINT pk_item PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    text      VARCHAR(512)                            NOT NULL,
    item_id   INTEGER REFERENCES items (id) ON DELETE CASCADE,
    author_id INTEGER REFERENCES users (id) ON DELETE CASCADE,
    CONSTRAINT pk_comment PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE,
    end_date   TIMESTAMP WITHOUT TIME ZONE,
    item_id    INTEGER REFERENCES items (id) ON DELETE CASCADE,
    booker_id  INTEGER REFERENCES users (id) ON DELETE CASCADE,
    status     VARCHAR(50),
    CONSTRAINT pk_booking PRIMARY KEY (id)
);