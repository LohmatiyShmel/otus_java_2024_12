create sequence client_SEQ start with 1 increment by 1;
create sequence address_SEQ start with 1 increment by 1;
create sequence phone_SEQ start with 1 increment by 1;

create table address
(
    id     bigint not null primary key default nextval('address_SEQ'),
    street varchar(255)
);

create table client
(
    id         bigint not null primary key default nextval('client_SEQ'),
    name       varchar(50),
    address_id bigint references address (id)
);

create table phone
(
    id        bigint not null primary key default nextval('phone_SEQ'),
    number    varchar(255),
    client_id bigint references client (id)
);
