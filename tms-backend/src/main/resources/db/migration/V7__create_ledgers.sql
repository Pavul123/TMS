-- V7: Central Financial Ledger
CREATE TABLE financial_transactions (
    id VARCHAR(32) PRIMARY KEY,
    date DATE NOT NULL,
    entity_type VARCHAR(32) NOT NULL,
    entity_id VARCHAR(32) NOT NULL,
    entity_name VARCHAR(128) NOT NULL,
    transaction_type VARCHAR(64) NOT NULL,
    category VARCHAR(64) NOT NULL,
    debit NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    credit NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    balance NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    payment_mode VARCHAR(32),
    reference_id VARCHAR(64),
    description TEXT NOT NULL,
    created_by VARCHAR(128) NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'POSTED',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_fin_txns_date ON financial_transactions(date);
CREATE INDEX idx_fin_txns_entity ON financial_transactions(entity_type, entity_id);
CREATE INDEX idx_fin_txns_type ON financial_transactions(transaction_type);
