--liquibase formatted sql
--changeset krasobas:create_result

CREATE TABLE IF NOT EXISTS results (
    id SERIAL PRIMARY KEY,
    first_arg DECIMAL,
    second_arg DECIMAL,
    result DECIMAL,
    operation TEXT,
    create_date TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);

--rollback DROP TABLE results;