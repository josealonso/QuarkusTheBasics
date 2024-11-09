
-- Insert sample data
INSERT INTO invoices (id, invoice_number, customer_name, invoice_date, amount, status, created_at, updated_at) VALUES
(1, '23', 'John Doe', '2023-01-15', 1500.00, 'PAID', '2023-01-15 10:00:00', '2023-01-15 10:00:00'),
(2, '5', 'Jane Smith', '2023-02-01', 2300.50, 'PENDING', '2023-02-01 11:30:00', '2023-02-01 11:30:00'),
(3, '7', 'Bob Johnson', '2023-02-15', 750.25, 'PAID', '2023-02-15 09:45:00', '2023-02-15 09:45:00'),
(4, '4', 'Alice Brown', '2023-03-01', 3000.00, 'OVERDUE', '2023-03-01 14:20:00', '2023-03-01 14:20:00'),
(5, '12', 'Charlie Wilson', '2023-03-15', 1200.75, 'PENDING', '2023-03-15 16:00:00', '2023-03-15 16:00:00'),
(6, '9', 'David Lee', '2023-04-01', 4500.00, 'PAID', '2023-04-01 12:15:00', '2023-04-01 12:15:00'),
(7, '3', 'Emily Davis', '2023-04-15', 2000.50, 'PENDING', '2023-04-15 10:30:00', '2023-04-15 10:30:00');

INSERT INTO users (id, username, password, email, roles) VALUES
(1, 'alice', '$2a$10$7BXE8bpTQsA6ZgZvqP5wEeq1eSIbBQjxs7Q2nUMXSGQIVCGHBULCu', 'alice', 'ROLE_ADMIN'),
(2, 'john', '$2a$10$0pQoVTkLzPnuGJCkAYzm0.Yl9g2bKF4/9xEUqCeXj7NAjgcfLhMZe', 'john', 'ROLE_USER');
