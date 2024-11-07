-- Create the invoices table
CREATE TABLE invoices (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    invoice_number VARCHAR(10) NOT NULL,
    customer_name VARCHAR(40) NOT NULL,
    invoice_date DATE NOT NULL,
    amount DECIMAL(10, 2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert sample data
INSERT INTO invoices (invoice_number, customer_name, invoice_date, amount, status, created_at, updated_at) VALUES
(1, '23', 'John Doe', '2023-01-15', 1500.00, 'PAID', '2023-01-15 10:00:00', '2023-01-15 10:00:00'),
(2, '5', 'Jane Smith', '2023-02-01', 2300.50, 'PENDING', '2023-02-01 11:30:00', '2023-02-01 11:30:00'),
(3, '7', 'Bob Johnson', '2023-02-15', 750.25, 'PAID', '2023-02-15 09:45:00', '2023-02-15 09:45:00'),
(4, '4', 'Alice Brown', '2023-03-01', 3000.00, 'OVERDUE', '2023-03-01 14:20:00', '2023-03-01 14:20:00'),
(5, '12', 'Charlie Wilson', '2023-03-15', 1200.75, 'PENDING', '2023-03-15 16:00:00', '2023-03-15 16:00:00');
