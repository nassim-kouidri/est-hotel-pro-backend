CREATE TABLE IF NOT EXISTS reservation
(
    id                 UUID         NOT NULL
        constraint pk_reservation primary key,
    hotel_room_id      UUID         NOT NULL,
    user_reservation   JSONB        NOT NULL,
    start_date         TIMESTAMP    NOT NULL,
    end_date           TIMESTAMP    NOT NULL,
    status             varchar(255) not null,
    number_of_children INT          NOT NULL,
    number_of_adults   INT          NOT NULL,
    price_paid         INT          NOT NULL,
    claim              VARCHAR(255),
    review             BIGINT,
    completed          BOOLEAN      NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_room FOREIGN KEY (hotel_room_id) REFERENCES hotel_room (id)
);
