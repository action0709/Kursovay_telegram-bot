-- liquibase formatted sql

-- changeset ACTION:1
CREATE TABLE notification_task(
id bigserial primary key,
chat_id bigint,
text varchar,
exec_date timestamp
)