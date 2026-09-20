-- V3: Rate Cards table
CREATE TABLE configured_rates (
    id VARCHAR(32) PRIMARY KEY,
    rate_type VARCHAR(32) NOT NULL,
    customer_id VARCHAR(32) REFERENCES customers(id) ON DELETE CASCADE,
    source_id VARCHAR(32) REFERENCES sources(id) ON DELETE CASCADE,
    material VARCHAR(128) NOT NULL,
    loading_location VARCHAR(128) NOT NULL,
    delivery_location VARCHAR(128) NOT NULL,
    rate NUMERIC(12,2) NOT NULL,
    unit VARCHAR(32) NOT NULL DEFAULT 'Ton',
    effective_from DATE NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_rates_lookup ON configured_rates(customer_id, material, loading_location, delivery_location);
