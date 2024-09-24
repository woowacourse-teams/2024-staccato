ALTER TABLE moment
    ADD COLUMN place_name VARCHAR(255) NULL;

UPDATE moment
SET place_name = address;

ALTER TABLE moment
    MODIFY COLUMN place_name VARCHAR(255) NOT NULL;
