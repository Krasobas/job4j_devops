--liquibase formatted sql
--changeset krasobas:alter_id_type_to_bigint
ALTER TABLE calc_events
ALTER COLUMN id TYPE BIGINT;