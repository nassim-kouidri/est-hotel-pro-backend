create table if not exists account
(
    id           UUID         not null
        constraint pk_account primary key,
    role         varchar(255) not null,
    name         varchar(255) not null
        constraint uk_name unique,
    first_name   varchar(255) not null,
    phone_number varchar(255) not null,
    password     text         not null
);