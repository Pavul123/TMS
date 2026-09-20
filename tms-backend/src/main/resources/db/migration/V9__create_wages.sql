-- V9: Worker and Driver Wages and Advances
CREATE TABLE worker_wages (
    id VARCHAR(32) PRIMARY KEY,
    worker_id VARCHAR(32) NOT NULL,
    worker_name VARCHAR(128) NOT NULL,
    month_year VARCHAR(10) NOT NULL,
    base_salary NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    overtime_amount NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    trip_allowance NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    advances_deducted NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    other_deductions NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    net_payable NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    payment_date DATE,
    payment_mode VARCHAR(32),
    reference_no VARCHAR(64),
    disbursed_by VARCHAR(128),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE wage_advances (
    id VARCHAR(32) PRIMARY KEY,
    date DATE NOT NULL,
    recipient_type VARCHAR(16) NOT NULL,
    recipient_id VARCHAR(32) NOT NULL,
    recipient_name VARCHAR(128) NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    reason TEXT,
    payment_mode VARCHAR(32) NOT NULL DEFAULT 'CASH',
    status VARCHAR(32) NOT NULL DEFAULT 'PAID',
    approved_by VARCHAR(128),
    given_by VARCHAR(128) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_wages_worker_id ON worker_wages(worker_id);
CREATE INDEX idx_advances_recipient ON wage_advances(recipient_type, recipient_id);
