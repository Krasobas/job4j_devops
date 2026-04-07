--liquibase formatted sql
--changeset krasobas:alter_users_add_columns
ALTER TABLE users
ADD COLUMN IF NOT EXISTS first_arg   BIGINT NOT NULL DEFAULT 0,
ADD COLUMN  IF NOT EXISTS second_arg  BIGINT NOT NULL DEFAULT 0,
ADD COLUMN  IF NOT EXISTS result      BIGINT NOT NULL DEFAULT 0;