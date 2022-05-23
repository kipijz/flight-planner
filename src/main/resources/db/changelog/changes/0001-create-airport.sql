-- liquibase formatted sql

-- changeset kristaps:1

CREATE TABLE airport
(
    airport varchar(255) not null primary key,
    country varchar(255) not null,
    city    varchar(255) not null
)