-- V11: Governance - Audit Trail and System Settings
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    entity_type VARCHAR(32) NOT NULL,
    entity_id VARCHAR(32) NOT NULL,
    action VARCHAR(32) NOT NULL,
    field_name VARCHAR(64),
    old_value TEXT,
    new_value TEXT,
    reason TEXT,
    performed_by VARCHAR(128) NOT NULL,
    performed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    ip_address VARCHAR(64),
    user_agent TEXT,
    approval_id VARCHAR(32)
);

CREATE TABLE company_settings (
    id INT PRIMARY KEY DEFAULT 1,
    company_name VARCHAR(128) NOT NULL DEFAULT 'TransFlow Logistics Pvt Ltd',
    gstin VARCHAR(32) DEFAULT '33AABCT1332L1Z8',
    address TEXT DEFAULT '124, Madurai Highway, Ramanathapuram, Tamil Nadu 623501',
    phone VARCHAR(32) DEFAULT '+91 98421 88001',
    email VARCHAR(128) DEFAULT 'contact@transflow.in',
    default_bank_account_id VARCHAR(32) DEFAULT 'ACC-002',
    invoice_prefix VARCHAR(16) DEFAULT 'INV-',
    trip_prefix VARCHAR(16) DEFAULT 'TRP-',
    credit_period_days INT DEFAULT 30,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_date ON audit_logs(performed_at);
CREATE INDEX idx_audit_user ON audit_logs(performed_by);
