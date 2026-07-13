
CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, 
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL            
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


CREATE TABLE IF NOT EXISTS refresh_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY, --
    token VARCHAR(255) NOT NULL,
    username VARCHAR(50) NOT NULL,  
    expiry_date DATETIME NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


INSERT INTO usuario (username, password, role) VALUES
(
    'admin',
    '$2a$10$Dow1s7x1uJk7kP8Gk9kF9e9Zz9mZ3z7fZ9yZQzQ9xZQzQ9xZQzQ9',
    'ADMIN' -- 
),
(
    'user',
    '$2a$10$Dow1s7x1uJk7kP8Gk9kF9e9Zz9mZ3z7fZ9yZQzQ9xZQzQ9xZQzQ9',
    'USER'  -- 
);

insert into usuario (username, password, role) values ('usuario1',
 '$2a$10$Dow1s7x1uJk7kP8Gk9kF9e9Zz9mZ3z7fZ9yZQzQ9xZQzQ9xZQzQ9', 'USER');