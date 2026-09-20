-- V6: Accounts, Payments and Contra Transfers
CREATE TABLE cash_bank_accounts (
    id VARCHAR(32) PRIMARY KEY,
    account_name VARCHAR(128) NOT NULL,
    account_type VARCHAR(32) NOT NULL,
    account_number VARCHAR(64),
    bank_name VARCHAR(128),
    ifsc VARCHAR(32),
    branch VARCHAR(128),
    balance NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payments (
    id VARCHAR(32) PRIMARY KEY,
    date DATE NOT NULL,
    customer_id VARCHAR(32) NOT NULL REFERENCES customers(id),
    customer_name VARCHAR(128) NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    payment_mode VARCHAR(32) NOT NULL,
    reference_number VARCHAR(64),
    account_id VARCHAR(32) NOT NULL REFERENCES cash_bank_accounts(id),
    status VARCHAR(32) NOT NULL DEFAULT 'POSTED',
    notes TEXT,
    received_by VARCHAR(128) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE payment_allocations (
    id BIGSERIAL PRIMARY KEY,
    payment_id VARCHAR(32) NOT NULL REFERENCES payments(id) ON DELETE CASCADE,
    invoice_id VARCHAR(32) NOT NULL REFERENCES invoices(id),
    allocated_amount NUMERIC(15,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE contra_transfers (
    id VARCHAR(32) PRIMARY KEY,
    transfer_date DATE NOT NULL,
    from_account_id VARCHAR(32) NOT NULL REFERENCES cash_bank_accounts(id),
    to_account_id VARCHAR(32) NOT NULL REFERENCES cash_bank_accounts(id),
    amount NUMERIC(15,2) NOT NULL,
    reference_number VARCHAR(64),
    reason TEXT,
    performed_by VARCHAR(128) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_payments_customer ON payments(customer_id);
CREATE INDEX idx_payments_date ON payments(date);
CREATE INDEX idx_allocations_payment ON payment_allocations(payment_id);
CREATE INDEX idx_allocations_invoice ON payment_allocations(invoice_id);
