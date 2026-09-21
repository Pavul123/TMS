-- V13: Allow direct billing items in invoices (relax trip_id foreign key constraint)
ALTER TABLE invoice_items DROP CONSTRAINT IF EXISTS invoice_items_trip_id_fkey;
ALTER TABLE invoice_items ALTER COLUMN trip_id DROP NOT NULL;
ALTER TABLE invoice_items ALTER COLUMN trip_id TYPE VARCHAR(64);
