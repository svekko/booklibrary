CREATE EXTENSION IF NOT EXISTS btree_gist;

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
    valid_to TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT valid_book_change EXCLUDE USING gist (book_id WITH =, tstzrange(valid_from, valid_to) WITH &&),
    CONSTRAINT unique_book_title EXCLUDE USING gist (trim(upper(title)) WITH =, tstzrange(valid_from, valid_to) WITH &&)
);

CREATE TABLE book_status_change (
    id SERIAL PRIMARY KEY,
    book_id INTEGER REFERENCES book (id) NOT NULL,
    book_status_id INTEGER REFERENCES book_status (id) NOT NULL,
    book_used_by_id INTEGER REFERENCES user_account (id),
    valid_from TIMESTAMP WITH TIME ZONE NOT NULL,
    valid_to TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT valid_book_status_change EXCLUDE USING gist (book_id WITH =, tstzrange(valid_from, valid_to) WITH &&)
);

INSERT INTO book_status (id, name)
    VALUES (1, 'Reserved'),
           (2, 'Borrowed');
