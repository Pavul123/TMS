-- V14: Relax customer address NOT NULL constraint for seamless imports and on-the-fly customer invoicing
ALTER TABLE customers ALTER COLUMN address DROP NOT NULL;
