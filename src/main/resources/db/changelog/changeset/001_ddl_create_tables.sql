--liquibase formatted sql
--changeset krasobas:create_users_table
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(2000)
);