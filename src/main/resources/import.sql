
DROP TABLE IF EXISTS invoices;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id BIGINT PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    roles VARCHAR(255)
);

CREATE TABLE invoices (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    invoice_number VARCHAR(255),
    customer_name VARCHAR(255),
    invoice_date VARCHAR(255),
    amount VARCHAR(255),
    status VARCHAR(255),
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
);

-- Insert users
INSERT INTO users (id, username, password, email, roles) VALUES
(1, 'alice', '$2a$10$7BXE8bpTQsA6ZgZvqP5wEeq1eSIbBQjxs7Q2nUMXSGQIVCGHBULCu', 'alice', 'ROLE_ADMIN'),
(2, 'john', '$2a$10$0pQoVTkLzPnuGJCkAYzm0.Yl9g2bKF4/9xEUqCeXj7NAjgcfLhMZe', 'john', 'ROLE_USER');

-- Insert invoices for alice (user_id = 1)
INSERT INTO invoices (id, invoice_number, customer_name, invoice_date, amount, status, created_at, updated_at, user_id) VALUES
(1, 'INV001', 'John Doe', '2023-01-15', '1500.00', 'PAID', '2023-01-15 10:00:00', '2023-01-15 10:00:00', 1),
(2, 'INV002', 'Jane Smith', '2023-02-01', '2300.50', 'PENDING', '2023-02-01 11:30:00', '2023-02-01 11:30:00', 1),
(3, 'INV003', 'Bob Johnson', '2023-02-15', '750.25', 'PAID', '2023-02-15 09:45:00', '2023-02-15 09:45:00', 1),
(4, 'INV004', 'Alice Brown', '2023-03-01', '3000.00', 'OVERDUE', '2023-03-01 14:20:00', '2023-03-01 14:20:00', 1),
(5, 'INV005', 'Charlie Wilson', '2023-03-15', '1200.75', 'PENDING', '2023-03-15 16:00:00', '2023-03-15 16:00:00', 1),
(6, 'INV006', 'David Lee', '2023-04-01', '4500.00', 'PAID', '2023-04-01 12:15:00', '2023-04-01 12:15:00', 1);

-- Insert invoices for john (user_id = 2)
INSERT INTO invoices (id, invoice_number, customer_name, invoice_date, amount, status, created_at, updated_at, user_id) VALUES
(7, 'INV007', 'Emily Davis', '2023-04-15', '2000.50', 'PENDING', '2023-04-15 10:30:00', '2023-04-15 10:30:00', 2),
(8, 'INV008', 'Frank White', '2023-01-15', '1500.00', 'PAID', '2023-01-15 10:00:00', '2023-01-15 10:00:00', 2),
(9, 'INV009', 'Grace Taylor', '2023-02-01', '2300.50', 'PENDING', '2023-02-01 11:30:00', '2023-02-01 11:30:00', 2),
(10, 'INV010', 'Henry Miller', '2023-04-15', '2000.50', 'PENDING', '2023-04-15 10:30:00', '2023-04-15 10:30:00', 2),
(11, 'INV011', 'Isabel Clark', '2023-03-15', '1200.75', 'PENDING', '2023-03-15 16:00:00', '2023-03-15 16:00:00', 2),
(12, 'INV012', 'Jack Wilson', '2023-04-01', '4500.00', 'PAID', '2023-04-01 12:15:00', '2023-04-01 12:15:00', 2);
