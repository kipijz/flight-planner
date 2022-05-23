-- liquibase formatted sql

-- changeset kristaps:1

CREATE TABLE flight
(
    id             serial primary key,
    from_id        varchar(255) not null,
    to_id          varchar(255) not null,
    carrier        varchar(255) not null,
    departure_time timestamp    not null,
    arrival_time   timestamp    not null,
    constraint flight_from_id_fkey foreign key (from_id) references airport (airport),
    constraint flight_to_id_fkey foreign key (to_id) references airport (airport)
)