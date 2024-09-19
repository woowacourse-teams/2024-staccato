ALTER TABLE member ADD COLUMN code VARCHAR(36) NOT NULL;
UPDATE member SET code = UUID() WHERE code WHERE code is '';
ALTER TABLE member ADD UNIQUE (code);
