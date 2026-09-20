-- V12: Initial Seed Data for TransFlow TMS
-- BCrypt hash for '{role}123' (e.g. $2a$10$wN0mfxUu7w/3O8w8E0xUoev.g/n.4gG5V9i7pU0nUa2j9k9w2rK6G or standard generated hash)
-- Roles
INSERT INTO roles (id, name, description) VALUES
('ROLE_ADMIN', 'ADMIN', 'System Administrator with total control'),
('ROLE_MD', 'MD', 'Managing Director with executive oversight and approval rights'),
('ROLE_MANAGER', 'MANAGER', 'Operations Manager with fleet and rate management rights'),
('ROLE_ACCOUNTS', 'ACCOUNTS', 'Financial Officer with invoicing and payment management rights'),
('ROLE_WORKER', 'WORKER', 'Data Entry Operator with trip logging rights');

-- Permissions
INSERT INTO permissions (id, name, module, description) VALUES
('TRIP_CREATE', 'Create Trips', 'OPERATIONS', 'Can log new operational trips'),
('TRIP_VIEW', 'View Trips', 'OPERATIONS', 'Can view trips list and details'),
('TRIP_EDIT_REQUEST', 'Request Trip Edit', 'OPERATIONS', 'Can submit correction requests for trips'),
('TRIP_APPROVE', 'Approve Trip Edits', 'GOVERNANCE', 'Can approve trip corrections'),
('CUSTOMER_VIEW', 'View Customers', 'MASTERS', 'Can search and view customer profiles'),
('CUSTOMER_MANAGE', 'Manage Customers', 'MASTERS', 'Can create and edit customer details'),
('VEHICLE_MANAGE', 'Manage Vehicles', 'MASTERS', 'Can manage vehicles fleet'),
('DRIVER_MANAGE', 'Manage Drivers', 'MASTERS', 'Can manage driver records'),
('RATE_MANAGE', 'Manage Rate Cards', 'MASTERS', 'Can configure customer and crusher rates'),
('INVOICE_CREATE', 'Create Invoices', 'FINANCE', 'Can generate GST invoices from trips'),
('INVOICE_VIEW', 'View Invoices', 'FINANCE', 'Can view customer invoices'),
('PAYMENT_CREATE', 'Record Payments', 'FINANCE', 'Can record customer payments and allocate'),
('PAYMENT_VIEW', 'View Payments', 'FINANCE', 'Can view payment receipts and ledgers'),
('PAYMENT_REVERSE', 'Reverse Payments', 'FINANCE', 'Can perform reversal transactions for invalid payments'),
('DIESEL_MANAGE', 'Manage Diesel', 'FINANCE', 'Can enter diesel receipts and view station balances'),
('WAGE_MANAGE', 'Manage Wages', 'FINANCE', 'Can disburse wages and record driver advances'),
('CASHBANK_MANAGE', 'Manage Cash and Bank', 'FINANCE', 'Can manage accounts and perform contra transfers'),
('AUDIT_VIEW', 'View Audit Logs', 'GOVERNANCE', 'Can inspect system audit logs'),
('USER_MANAGE', 'Manage Users', 'SECURITY', 'Can create, edit and disable users'),
('SETTINGS_MANAGE', 'Manage Settings', 'SYSTEM', 'Can configure company profile and numbering');

-- Role Permissions Map
-- Admin: All permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT 'ROLE_ADMIN', id FROM permissions;

-- MD: Executive Permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
('ROLE_MD', 'TRIP_VIEW'),
('ROLE_MD', 'TRIP_APPROVE'),
('ROLE_MD', 'CUSTOMER_VIEW'),
('ROLE_MD', 'INVOICE_VIEW'),
('ROLE_MD', 'PAYMENT_VIEW'),
('ROLE_MD', 'AUDIT_VIEW');

-- Manager: Operations Permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
('ROLE_MANAGER', 'TRIP_CREATE'),
('ROLE_MANAGER', 'TRIP_VIEW'),
('ROLE_MANAGER', 'TRIP_EDIT_REQUEST'),
('ROLE_MANAGER', 'TRIP_APPROVE'),
('ROLE_MANAGER', 'CUSTOMER_VIEW'),
('ROLE_MANAGER', 'CUSTOMER_MANAGE'),
('ROLE_MANAGER', 'VEHICLE_MANAGE'),
('ROLE_MANAGER', 'DRIVER_MANAGE'),
('ROLE_MANAGER', 'RATE_MANAGE');

-- Accounts: Finance Permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
('ROLE_ACCOUNTS', 'CUSTOMER_VIEW'),
('ROLE_ACCOUNTS', 'INVOICE_CREATE'),
('ROLE_ACCOUNTS', 'INVOICE_VIEW'),
('ROLE_ACCOUNTS', 'PAYMENT_CREATE'),
('ROLE_ACCOUNTS', 'PAYMENT_VIEW'),
('ROLE_ACCOUNTS', 'PAYMENT_REVERSE'),
('ROLE_ACCOUNTS', 'DIESEL_MANAGE'),
('ROLE_ACCOUNTS', 'WAGE_MANAGE'),
('ROLE_ACCOUNTS', 'CASHBANK_MANAGE');

-- Worker: Restricted Entry Permissions
INSERT INTO role_permissions (role_id, permission_id) VALUES
('ROLE_WORKER', 'TRIP_CREATE'),
('ROLE_WORKER', 'TRIP_VIEW'),
('ROLE_WORKER', 'TRIP_EDIT_REQUEST'),
('ROLE_WORKER', 'CUSTOMER_VIEW');

-- Default Seed Users (Password: {username}123 encoded with BCrypt: $2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG)
-- Hash '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG' is BCrypt for 'password123'
INSERT INTO users (id, username, password_hash, full_name, email, phone, role_id, status) VALUES
('USR-001', 'admin', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'System Administrator', 'admin@transflow.in', '+91 98421 88001', 'ROLE_ADMIN', 'ACTIVE'),
('USR-002', 'md', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'M. Ramanathan (MD)', 'md@transflow.in', '+91 98421 88002', 'ROLE_MD', 'ACTIVE'),
('USR-003', 'manager', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'S. Senthil (Operations Manager)', 'manager@transflow.in', '+91 98421 88003', 'ROLE_MANAGER', 'ACTIVE'),
('USR-004', 'accounts', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'K. Venkat (Senior Accountant)', 'accounts@transflow.in', '+91 98421 88004', 'ROLE_ACCOUNTS', 'ACTIVE'),
('USR-005', 'worker', '$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG', 'Arun Kumar (Dispatcher)', 'worker@transflow.in', '+91 98421 88005', 'ROLE_WORKER', 'ACTIVE');

-- Initial Masters
INSERT INTO customers (id, name, phone, alternate_phone, address, gstin, credit_terms, opening_balance, status) VALUES
('CUS-00124', 'K Engineering Infra Projects', '+91 94431 52671', '+91 98421 11223', 'National Highway 49, Paramakudi, Ramanathapuram Dist.', '33AABCK1234F1Z5', '30 Days', 1355000.00, 'ACTIVE'),
('CUS-00125', 'DBL Highway Construction Ltd', '+91 98421 88442', '+91 98421 88443', 'Bypass Ring Road Project Office, Ramanathapuram', '33AABCD9876E1Z2', '15 Days', 450000.00, 'ACTIVE'),
('CUS-00126', 'Rajaganapathy Builders', '+91 97882 33441', NULL, 'East Coast Road, Rameswaram Branch', '33AABCR5544G1Z9', '7 Days', 120000.00, 'ACTIVE');

INSERT INTO vehicles (registration, type, ownership, capacity, fuel_capacity, current_km, status) VALUES
('TN 58 AB 2345', '10-Wheel Tipper', 'OWN', '18 Ton', '180 L', 142500.00, 'AVAILABLE'),
('TN 65 CD 8821', '12-Wheel Tipper', 'OWN', '20 Ton', '220 L', 98400.00, 'AVAILABLE'),
('TN 58 EF 4410', '10-Wheel Tipper', 'OWN', '18 Ton', '180 L', 210500.00, 'AVAILABLE'),
('TN 67 GH 9901', '6-Wheel Medium Tipper', 'RENTED', '12 Ton', '140 L', 65200.00, 'AVAILABLE');

INSERT INTO drivers (id, name, phone, license_number, assigned_vehicle, advance_balance, status) VALUES
('DRV-0012', 'M. Murugan', '+91 98421 66101', 'TN-65-2018-004412', 'TN 58 AB 2345', 2500.00, 'AVAILABLE'),
('DRV-0013', 'K. Rajendran', '+91 97882 11045', 'TN-58-2016-009821', 'TN 65 CD 8821', 1200.00, 'AVAILABLE'),
('DRV-0014', 'P. Ganesan', '+91 94431 88720', 'TN-67-2020-001289', 'TN 58 EF 4410', 0.00, 'AVAILABLE');

INSERT INTO workers (id, name, phone, email, role, system_role, salary, status) VALUES
('WRK-0024', 'Arun Kumar', '+91 98421 88005', 'arun@transflow.in', 'Data Entry Operator', 'WORKER', 18000.00, 'ACTIVE'),
('WRK-0025', 'S. Senthil', '+91 98421 88003', 'senthil@transflow.in', 'Supervisor', 'MANAGER', 35000.00, 'ACTIVE'),
('WRK-0026', 'K. Venkat', '+91 98421 88004', 'venkat@transflow.in', 'Accounts', 'ACCOUNTS', 40000.00, 'ACTIVE');

INSERT INTO sources (id, name, location, material, price_per_ton, effective_from, status) VALUES
('SRC-001', 'Sri Ramanatha Blue Metals Crusher', 'Sayalgudi Quarry Area, Ramnad', '20 MM Blue Metal', 420.00, '2026-01-01', 'ACTIVE'),
('SRC-002', 'Ayyappan Quarry & Crushers', 'Uchipuli Hills, Ramanathapuram', 'Black M-Sand', 480.00, '2026-01-01', 'ACTIVE'),
('SRC-003', 'Valinokkam Boulders Depot', 'Valinokkam Coast Area', 'Heavy Boulders', 350.00, '2026-01-01', 'ACTIVE');

INSERT INTO materials (id, name, category, standard_unit, status) VALUES
('MAT-001', '20 MM Aggregate', 'Coarse Aggregate', 'Ton', 'ACTIVE'),
('MAT-002', '12 MM Aggregate', 'Coarse Aggregate', 'Ton', 'ACTIVE'),
('MAT-003', 'Black M-Sand', 'Manufactured Sand', 'Ton', 'ACTIVE'),
('MAT-004', 'Boulders (Sea Wall / Base)', 'Raw Stone', 'Ton', 'ACTIVE'),
('MAT-005', 'WMM (Wet Mix Macadam)', 'Road Base', 'Ton', 'ACTIVE');

INSERT INTO locations (id, name, type, address, distance_km, status) VALUES
('LOC-001', 'Paramakudi Highway Site KM 42', 'Site', 'NH 49 Extension, Paramakudi', 38.5, 'ACTIVE'),
('LOC-002', 'Rameswaram Port Coastal Yard', 'Yard', 'Pamban Coastal Road, Rameswaram', 52.0, 'ACTIVE'),
('LOC-003', 'Ramnad Ring Road Bypass Phase-2', 'Site', 'Collectorate Bypass, Ramanathapuram', 14.0, 'ACTIVE');

INSERT INTO fuel_stations (id, name, location, contact_person, phone, balance, status) VALUES
('PMP-001', 'Indian Oil Corporation - Ramnad Bypass', 'NH 49, Ramanathapuram', 'P. Selvam', '+91 94431 22900', 48500.00, 'ACTIVE'),
('PMP-002', 'Bharat Petroleum - Paramakudi', 'Paramakudi Town', 'R. Muthu', '+91 97882 66700', 12000.00, 'ACTIVE');

INSERT INTO cash_bank_accounts (id, account_name, account_type, account_number, bank_name, ifsc, branch, balance, status) VALUES
('ACC-001', 'Office Cash in Hand', 'CASH', NULL, NULL, NULL, 'Head Office Safe', 85400.00, 'ACTIVE'),
('ACC-002', 'HDFC Corporate Current A/c', 'BANK', '50200088991122', 'HDFC Bank', 'HDFC0001420', 'Ramanathapuram Main Branch', 645000.00, 'ACTIVE'),
('ACC-003', 'SBI Operating A/c', 'BANK', '38901234567', 'State Bank of India', 'SBIN0000910', 'Ramnad Collectorate Branch', 210000.00, 'ACTIVE');

INSERT INTO configured_rates (id, rate_type, customer_id, material, loading_location, delivery_location, rate, unit, effective_from, status) VALUES
('RAT-001', 'CUSTOMER', 'CUS-00124', '20 MM Aggregate', 'Sri Ramanatha Blue Metals Crusher', 'Paramakudi Highway Site KM 42', 780.00, 'Ton', '2026-01-01', 'ACTIVE'),
('RAT-002', 'CUSTOMER', 'CUS-00124', 'Black M-Sand', 'Ayyappan Quarry & Crushers', 'Paramakudi Highway Site KM 42', 820.00, 'Ton', '2026-01-01', 'ACTIVE'),
('RAT-003', 'CUSTOMER', 'CUS-00125', 'Boulders (Sea Wall / Base)', 'Valinokkam Boulders Depot', 'Rameswaram Port Coastal Yard', 650.00, 'Ton', '2026-01-01', 'ACTIVE');

INSERT INTO company_settings (id, company_name, gstin, address, phone, email) VALUES
(1, 'TransFlow Logistics Pvt Ltd', '33AABCT1332L1Z8', '124, Madurai Highway, Ramanathapuram, Tamil Nadu 623501', '+91 98421 88001', 'contact@transflow.in')
ON CONFLICT (id) DO NOTHING;
