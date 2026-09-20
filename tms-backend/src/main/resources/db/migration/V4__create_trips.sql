-- V4: Trips and Trip Status History
CREATE TABLE trips (
    id VARCHAR(32) PRIMARY KEY,
    date DATE NOT NULL,
    customer_id VARCHAR(32) NOT NULL REFERENCES customers(id),
    customer_name VARCHAR(128) NOT NULL,
    customer_phone VARCHAR(32) NOT NULL,
    vehicle_registration VARCHAR(32) NOT NULL REFERENCES vehicles(registration),
    vehicle_ownership VARCHAR(32) NOT NULL DEFAULT 'OWN',
    driver_id VARCHAR(32) NOT NULL REFERENCES drivers(id),
    driver_name VARCHAR(128) NOT NULL,
    driver_phone VARCHAR(32) NOT NULL,
    material VARCHAR(128) NOT NULL,
    quantity NUMERIC(12,2) NOT NULL,
    unit VARCHAR(32) NOT NULL DEFAULT 'Ton',
    source VARCHAR(128) NOT NULL,
    source_bill_no VARCHAR(64),
    loading_location VARCHAR(128) NOT NULL,
    delivery_location VARCHAR(128) NOT NULL,
    loading_date_time TIMESTAMP,
    departure_date_time TIMESTAMP,
    delivery_date_time TIMESTAMP,
    unload_quantity NUMERIC(12,2),
    unload_unit VARCHAR(32),
    shortage NUMERIC(12,2) DEFAULT 0.00,
    opening_km NUMERIC(12,2),
    closing_km NUMERIC(12,2),
    trip_km NUMERIC(12,2),
    applied_rate NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    rate_unit VARCHAR(32) NOT NULL DEFAULT 'Ton',
    total_amount NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(32) NOT NULL DEFAULT 'DELIVERED',
    progress INT NOT NULL DEFAULT 7,
    is_no_load BOOLEAN NOT NULL DEFAULT FALSE,
    no_load_reason VARCHAR(255),
    entered_by VARCHAR(128) NOT NULL,
    notes TEXT,
    delivery_proof TEXT,
    invoice_id VARCHAR(32),
    version BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE trip_status_history (
    id BIGSERIAL PRIMARY KEY,
    trip_id VARCHAR(32) NOT NULL REFERENCES trips(id) ON DELETE CASCADE,
    old_status VARCHAR(32),
    new_status VARCHAR(32) NOT NULL,
    changed_by VARCHAR(128) NOT NULL,
    comment TEXT,
    changed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_trips_date ON trips(date);
CREATE INDEX idx_trips_customer_id ON trips(customer_id);
CREATE INDEX idx_trips_vehicle ON trips(vehicle_registration);
CREATE INDEX idx_trips_driver_id ON trips(driver_id);
CREATE INDEX idx_trips_status ON trips(status);
CREATE INDEX idx_trips_invoice_id ON trips(invoice_id);
