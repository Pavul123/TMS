-- V10: Governance - Correction Requests & Approval Engine
CREATE TABLE correction_requests (
    id VARCHAR(32) PRIMARY KEY,
    entity_type VARCHAR(32) NOT NULL,
    entity_id VARCHAR(32) NOT NULL,
    entity_identifier VARCHAR(128) NOT NULL,
    reason TEXT NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'PENDING',
    requested_by VARCHAR(128) NOT NULL,
    requested_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    reviewed_by VARCHAR(128),
    reviewed_at TIMESTAMP,
    review_comment TEXT,
    version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE correction_request_items (
    id BIGSERIAL PRIMARY KEY,
    request_id VARCHAR(32) NOT NULL REFERENCES correction_requests(id) ON DELETE CASCADE,
    field_name VARCHAR(64) NOT NULL,
    old_value TEXT,
    requested_value TEXT NOT NULL
);

CREATE INDEX idx_corr_status ON correction_requests(status);
CREATE INDEX idx_corr_entity ON correction_requests(entity_type, entity_id);
