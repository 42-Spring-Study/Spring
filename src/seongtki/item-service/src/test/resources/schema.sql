drop table account if exists cascade;
create table account (
    account_id varchar(10),
    account integer not null default 0,
    primary key (account_id)
);
-- item
drop table if exists item CASCADE;
create table item
(
    id bigint generated by default as identity,
    item_name varchar(10),
    price integer,
    quantity integer,
    primary key (id)
);
