-- V2: Master tables
CREATE TABLE customers (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    phone VARCHAR(32) NOT NULL UNIQUE,
    alternate_phone VARCHAR(32),
    address TEXT NOT NULL,
    gstin VARCHAR(32),
    credit_terms VARCHAR(64),
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    notes TEXT,
    opening_balance NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE vehicles (
    registration VARCHAR(32) PRIMARY KEY,
    type VARCHAR(64) NOT NULL,
    ownership VARCHAR(32) NOT NULL DEFAULT 'OWN',
    capacity VARCHAR(32) NOT NULL,
    fuel_capacity VARCHAR(32) NOT NULL,
    current_km NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    last_maintenance_date DATE,
    maintenance_fee NUMERIC(12,2),
    insurance_expiry DATE,
    fc_expiry DATE,
    permit_expiry DATE,
    assigned_driver_id VARCHAR(32),
    assigned_driver_name VARCHAR(128),
    status VARCHAR(32) NOT NULL DEFAULT 'AVAILABLE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE drivers (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    phone VARCHAR(32) NOT NULL,
    license_number VARCHAR(64),
    assigned_vehicle VARCHAR(32),
    status VARCHAR(32) NOT NULL DEFAULT 'AVAILABLE',
    advance_balance NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    total_earnings NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    total_settled NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE workers (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    phone VARCHAR(32) NOT NULL,
    email VARCHAR(128),
    role VARCHAR(64) NOT NULL,
    system_role VARCHAR(32) NOT NULL,
    salary NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    paid NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    advance NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    deduction NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    assigned_location VARCHAR(128),
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    last_login TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE sources (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    location VARCHAR(128) NOT NULL,
    contact_person VARCHAR(128),
    phone VARCHAR(32),
    material VARCHAR(64) NOT NULL,
    price_per_ton NUMERIC(12,2) NOT NULL DEFAULT 0.00,
    effective_from DATE NOT NULL,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE materials (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(128) NOT NULL UNIQUE,
    category VARCHAR(64) NOT NULL,
    standard_unit VARCHAR(32) NOT NULL DEFAULT 'Ton',
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE locations (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    type VARCHAR(32) NOT NULL,
    address TEXT,
    distance_km NUMERIC(10,2),
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE fuel_stations (
    id VARCHAR(32) PRIMARY KEY,
    name VARCHAR(128) NOT NULL,
    location VARCHAR(128) NOT NULL,
    contact_person VARCHAR(128),
    phone VARCHAR(32),
    balance NUMERIC(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_customers_phone ON customers(phone);
CREATE INDEX idx_vehicles_status ON vehicles(status);
CREATE INDEX idx_drivers_status ON drivers(status);
