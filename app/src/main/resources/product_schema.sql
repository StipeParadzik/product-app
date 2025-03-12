CREATE TABLE products (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    price_eur NUMERIC(10,2) NOT NULL,
    is_available BOOLEAN NOT NULL
);
