create table if not exists hotel_room
(
    id          UUID         not null
        constraint pk_hotel_room primary key,
    category    varchar(255) not null,
    room_number int          not null
        constraint uk_number_room unique,
    price       int          not null,
    created_at  timestamp,
    updated_at  timestamp,
    state       varchar(255),
    image_url   varchar(255),
    available   boolean      not null default true,
    version     int
);