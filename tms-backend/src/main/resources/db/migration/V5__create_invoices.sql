-- V5: Invoices and Invoice Line Items
CREATE TABLE invoices (
    id VARCHAR(32) PRIMARY KEY,
    date DATE NOT NULL,
    due_date DATE,
    customer_id VARCHAR(32) NOT NULL REFERENCES customers(id),
    customer_name VARCHAR(128) NOT NULL,
    customer_gstin VARCHAR(32),
    subtotal NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    tax_rate NUMERIC(5,2) NOT NULL DEFAULT 0.00,
    tax_amount NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    total_amount NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    paid_amount NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    balance_amount NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(32) NOT NULL DEFAULT 'GENERATED',
    notes TEXT,
    generated_by VARCHAR(128) NOT NULL,
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE invoice_items (
    id BIGSERIAL PRIMARY KEY,
    invoice_id VARCHAR(32) NOT NULL REFERENCES invoices(id) ON DELETE CASCADE,
    trip_id VARCHAR(32) NOT NULL REFERENCES trips(id),
    trip_date DATE NOT NULL,
    vehicle VARCHAR(32) NOT NULL,
    material VARCHAR(128) NOT NULL,
    quantity NUMERIC(12,2) NOT NULL,
    unit VARCHAR(32) NOT NULL DEFAULT 'Ton',
    rate NUMERIC(12,2) NOT NULL,
    amount NUMERIC(15,2) NOT NULL
);

CREATE INDEX idx_invoices_customer_id ON invoices(customer_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_date ON invoices(date);
CREATE INDEX idx_invoice_items_invoice_id ON invoice_items(invoice_id);
CREATE INDEX idx_invoice_items_trip_id ON invoice_items(trip_id);
