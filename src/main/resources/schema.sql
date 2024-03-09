DROP TABLE IF EXISTS users, requests, items, bookings, comments;

CREATE TABLE IF NOT EXISTS users (
id            INT GENERATED     BY DEFAULT AS IDENTITY PRIMARY KEY,
email         VARCHAR(40)       NOT NULL,
name          VARCHAR(20)       NOT NULL,
UNIQUE (email)
);

CREATE TABLE IF NOT EXISTS requests (
id            INT GENERATED     BY DEFAULT AS IDENTITY PRIMARY KEY,
description   VARCHAR(200)      NOT NULL,
requestor_id  INT               REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS items (
id            INT GENERATED     BY DEFAULT AS IDENTITY PRIMARY KEY,
name          VARCHAR(20)       NOT NULL,
description   VARCHAR(200)      NOT NULL,
available     BOOLEAN           NOT NULL,
owner_id      INT               REFERENCES users(id),
request_id    INT               REFERENCES requests(id)
);


CREATE TABLE IF NOT EXISTS bookings (
id            INT GENERATED     BY DEFAULT AS IDENTITY PRIMARY KEY,
start_date    TIMESTAMP,
end_date      TIMESTAMP,
item_id       INT               REFERENCES items(id),
booker_id     INT               REFERENCES users(id),
status        VARCHAR(10)       NOT NULL
);

CREATE TABLE IF NOT EXISTS comments (
id            INT GENERATED     BY DEFAULT AS IDENTITY PRIMARY KEY,
text          VARCHAR(200)      NOT NULL,
item_id       INT               REFERENCES items(id),
author_id     INT               REFERENCES users(id),
created_date  TIMESTAMP         NOT NULL
);