-- liquibase formatted sql

-- changeset laterna:1
CREATE TABLE channel_members
(
    id         BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    channel_id BIGINT REFERENCES channels (id) ON DELETE CASCADE NOT NULL,
    user_id    BIGINT REFERENCES users (id)                      NOT NULL,
    joined_at  TIMESTAMPTZ                                       NOT NULL,
    UNIQUE (channel_id, user_id)
);
-- rollback DROP TABLE channel_members;