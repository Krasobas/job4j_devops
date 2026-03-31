--liquibase formatted sql
--changeset krasobas:alter_users_add_columns
ALTER TABLE users
ADD COLUMN  first_arg   BIGINT NOT NULL DEFAULT 0,
ADD COLUMN  second_arg  BIGINT NOT NULL DEFAULT 0,
ADD COLUMN  result      BIGINT NOT NULL DEFAULT 0;