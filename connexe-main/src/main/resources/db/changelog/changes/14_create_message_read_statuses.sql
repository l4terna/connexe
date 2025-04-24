-- liquibase formatted sql

-- changeset laterna:1
CREATE SEQUENCE message_read_statuses_seq
    INCREMENT BY 30
    START WITH 1
    MINVALUE 1
    NO MAXVALUE CACHE 50;

CREATE TABLE message_read_statuses
(
    id         BIGINT DEFAULT nextval('message_read_statuses_seq') PRIMARY KEY,
    message_id BIGINT REFERENCES messages (id) ON DELETE CASCADE NOT NULL,
    user_id    BIGINT REFERENCES users (id) ON DELETE CASCADE    NOT NULL,
    read_at    TIMESTAMPTZ,
    UNIQUE (message_id, user_id)
);

-- rollback DROP TABLE message_read_statuses;