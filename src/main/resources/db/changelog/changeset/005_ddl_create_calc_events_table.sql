--liquibase formatted sql
--changeset krasobas:create_calc_events_table
CREATE TABLE IF NOT EXISTS calc_events (
    id BIG SERIAL PRIMARY KEY,
    user_id BIGINT,
    first BIGINT,
    second BIGINT,
    result BIGINT,
    create_date TIMESTAMP,
    type VARCHAR(255)
);