-- LoopLab schema v3: allow ISC (Class 11 & 12) quiz and exam attempts.
-- Data-only change to a CHECK constraint; previous releases keep working against it.

ALTER TABLE attempts DROP CONSTRAINT ck_attempts_curriculum;
ALTER TABLE attempts ADD CONSTRAINT ck_attempts_curriculum CHECK (curriculum IN ('APCS', 'ICSE', 'ISC'));
