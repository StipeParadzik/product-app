CREATE TABLE IF NOT EXISTS products (
    id SERIAL PRIMARY KEY,
    code VARCHAR(10) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    price_eur DECIMAL(10,2) NOT NULL CHECK (price_eur >= 0),
    is_available BOOLEAN NOT NULL
);

CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL
);

INSERT INTO users (username, password, role) VALUES (
    'admin',
    '$2a$10$r8pud98i8eD.IxrctXbpPeI2GA7wGPD8wxByPr7bxTsiYrsShAAqW', 
    'ROLE_ADMIN'
);