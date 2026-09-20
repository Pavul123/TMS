-- V8: Diesel and Vehicle Maintenance Expenses
CREATE TABLE diesel_logs (
    id VARCHAR(32) PRIMARY KEY,
    date DATE NOT NULL,
    vehicle_registration VARCHAR(32) NOT NULL REFERENCES vehicles(registration),
    driver_id VARCHAR(32) NOT NULL REFERENCES drivers(id),
    driver_name VARCHAR(128) NOT NULL,
    fuel_station_id VARCHAR(32) NOT NULL REFERENCES fuel_stations(id),
    fuel_station_name VARCHAR(128) NOT NULL,
    bill_number VARCHAR(64),
    litres NUMERIC(10,2) NOT NULL,
    rate_per_litre NUMERIC(10,2) NOT NULL,
    total_amount NUMERIC(15,2) NOT NULL,
    start_km NUMERIC(12,2),
    end_km NUMERIC(12,2),
    km_run NUMERIC(12,2),
    mileage NUMERIC(8,2),
    payment_mode VARCHAR(32) NOT NULL DEFAULT 'CREDIT',
    status VARCHAR(32) NOT NULL DEFAULT 'RECORDED',
    notes TEXT,
    entered_by VARCHAR(128) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vehicle_expenses (
    id VARCHAR(32) PRIMARY KEY,
    date DATE NOT NULL,
    vehicle_registration VARCHAR(32) NOT NULL REFERENCES vehicles(registration),
    expense_type VARCHAR(64) NOT NULL,
    amount NUMERIC(15,2) NOT NULL,
    vendor_name VARCHAR(128),
    invoice_bill_no VARCHAR(64),
    payment_mode VARCHAR(32) NOT NULL DEFAULT 'CASH',
    status VARCHAR(32) NOT NULL DEFAULT 'PAID',
    description TEXT,
    entered_by VARCHAR(128) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_diesel_vehicle ON diesel_logs(vehicle_registration);
CREATE INDEX idx_diesel_date ON diesel_logs(date);
CREATE INDEX idx_expenses_vehicle ON vehicle_expenses(vehicle_registration);
