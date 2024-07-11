-- Active: 1720705173932@@127.0.0.1@54321@db_spring_test_api
CREATE USER spring_test;

Create DATABASE db_spring_test_api;

GRANT ALL PRIVILEGES ON DATABASE db_spring_test_api to spring_test;

ALTER USER spring_test WITH PASSWORD 'toor';

SELECT current_database();

SELECT *
FROM information_schema.tables
WHERE
    table_schema = 'public'