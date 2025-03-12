CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    price_eur DECIMAL(10,2) NOT NULL CHECK (price_eur >= 0),
    is_available BOOLEAN NOT NULL
);
