-- TraceTables schema v4: remember each student's last sign-in, so the dashboard can tell a first visit
-- ("Welcome to TraceTables") from a returning one ("Welcome back").
-- Only adds a column, so the previous release keeps working against this schema (safe rollback).

ALTER TABLE students ADD COLUMN last_login_at TIMESTAMP WITH TIME ZONE;

-- Accounts that already exist are treated as returning students.
UPDATE students SET last_login_at = created_at;
