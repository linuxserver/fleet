-- Specifically for deprecation
ALTER TABLE Images
ADD COLUMN `deprecated`         TINYINT NOT NULL DEFAULT 0,
ADD COLUMN `deprecation_reason` VARCHAR(255);