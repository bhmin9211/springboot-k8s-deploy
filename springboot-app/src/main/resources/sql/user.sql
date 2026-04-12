CREATE DATABASE k8s_dashboard DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- users table
CREATE TABLE IF NOT EXISTS k8s_dashboard.users (
                                                   id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                                   username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    );

-- sample user
INSERT INTO k8s_dashboard.USERS (username, password, role)
VALUES (
           'demo',
           '$2a$10$Jj18gd1ygLics1ppgCXZRua5kh1SbViFjW9Fimj39QHkU7GT2Pw6K',  -- 'test1234'
           'USER'
       );
