-- Active: 1720705173932@@127.0.0.1@54321@db_spring_test_api
CREATE USER spring_test;

Create DATABASE db_spring_test_api;

GRANT ALL PRIVILEGES ON DATABASE db_spring_test_api to spring_test;

ALTER USER spring_test WITH PASSWORD 'toor';

SELECT current_database();
SELECT current_user;

SELECT *
FROM information_schema.tables
WHERE
    table_schema = 'public';


--Create Users Entity
CREATE TABLE public.users (
	id bigserial NOT NULL,
	username varchar NOT NULL,
	"password" varchar NOT NULL,
	"name" varchar NOT NULL,
	"token" varchar NULL,
	token_expired_at bigint NULL,
	CONSTRAINT users_pk PRIMARY KEY (id),
	CONSTRAINT users_un_username UNIQUE (username),
	CONSTRAINT users_un_token UNIQUE ("token")
);

select * from users;

SELECT column_name, data_type, character_maximum_length FROM information_schema. columns WHERE table_name = 'users';

select pg_get_serial_sequence('users', 'id');


--Create Contact Entity
CREATE TABLE public.contacts (
    id bigserial NOT NULL,
    user_id bigint NOT NULL,
    first_name varchar NOT NULL,
    last_name varchar,
    phone varchar,
    email varchar,
    constraint contact_pk primary key (id),
    foreign key (user_id) references users (id)
);

select * from contacts;

SELECT * FROM information_schema. columns WHERE table_name = 'contacts';

select pg_get_serial_sequence('contacts', 'id');

ALTER TABLE public.contacts ALTER COLUMN id ADD GENERATED ALWAYS AS IDENTITY;

--Create Address Entity
CREATE TABLE public.addresses (
    id bigserial not null,
    contact_id bigint not null,
    street varchar,
    city varchar,
    province varchar,
    country varchar not null,
    postal_code varchar,
    constraint address_pk primary key (id),
    foreign key (contact_id) references contacts (id)
)

select * from addresses

SELECT column_name, data_type, character_maximum_length FROM information_schema. columns WHERE table_name = 'addresses';