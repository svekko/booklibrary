CREATE TABLE user_account (
    id SERIAL PRIMARY KEY,
    email VARCHAR(256) NOT NULL UNIQUE,
    password VARCHAR(256) NOT NULL
);

CREATE TABLE book_status (
    id INTEGER PRIMARY KEY,
    name VARCHAR(128) NOT NULL
);

CREATE TABLE book (
    id SERIAL PRIMARY KEY
);

CREATE TABLE book_change (
    id SERIAL PRIMARY KEY,
    title VARCHAR(256) NOT NULL,
    book_id INTEGER REFERENCES book (id) NOT NULL,
    created_by_id INTEGER REFERENCES user_account (id) NOT NULL,
    valid_from TIMESTAMP WITH TIME ZONE NOT NULL,
    valid_to TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE book_status_change (
    id SERIAL PRIMARY KEY,
    book_id INTEGER REFERENCES book (id) NOT NULL,
    book_status_id INTEGER REFERENCES book_status (id) NOT NULL,
    book_used_by_id INTEGER REFERENCES user_account (id),
    valid_from TIMESTAMP WITH TIME ZONE NOT NULL,
    valid_to TIMESTAMP WITH TIME ZONE NOT NULL
);

INSERT INTO book_status (id, name)
    VALUES (1, 'Reserved'),
           (2, 'Borrowed');

INSERT INTO user_account (email, password)
    VALUES ('test1@local.host', '$2a$10$BMBf6Da2ZuSWBZpAZm2Vb.JvDkbCjpLlNCnWKMNVlV7wDxVKrzNcy' /* abc123 */),
           ('test2@local.host', '$2a$10$BMBf6Da2ZuSWBZpAZm2Vb.JvDkbCjpLlNCnWKMNVlV7wDxVKrzNcy' /* abc123 */),
           ('test3@local.host', '$2a$10$BMBf6Da2ZuSWBZpAZm2Vb.JvDkbCjpLlNCnWKMNVlV7wDxVKrzNcy' /* abc123 */);
