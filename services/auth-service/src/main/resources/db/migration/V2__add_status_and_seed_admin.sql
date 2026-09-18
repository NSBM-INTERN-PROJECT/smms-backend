-- ─────────────────────────────────────────────────────────────
-- Auth Service — V2 Migration
-- Adds: status column, must_change_password flag, seeds admin account
--
-- Admin seed credentials (CHANGE ON FIRST LOGIN):
--   Email:    admin@smms.lk
--   Password: Admin@2024  (BCrypt hash below, strength 12)
-- ─────────────────────────────────────────────────────────────

-- 1. Add status column (replaces boolean is_active with a richer enum)
ALTER TABLE users
    ADD COLUMN status ENUM('ACTIVE', 'INACTIVE', 'LOCKED') NOT NULL DEFAULT 'ACTIVE' AFTER role,
    ADD COLUMN must_change_password BOOLEAN NOT NULL DEFAULT FALSE AFTER status,
    ADD COLUMN full_name VARCHAR(150) NOT NULL DEFAULT '' AFTER username;

-- 2. Backfill status from is_active
UPDATE users SET status = CASE WHEN is_active = TRUE THEN 'ACTIVE' ELSE 'INACTIVE' END;

-- 3. Seed default Admin account
--    Password: Admin@2024
--    BCrypt hash (strength 12) — generated offline, safe to store in migration
INSERT INTO users (username, email, full_name, password_hash, role, status, must_change_password)
VALUES (
    'admin',
    'admin@smms.lk',
    'System Administrator',
    '$2a$12$8K1p/a0dhrxSA8osqYLiR.sJNK4xh3yFTWxjDLkuB2W7C.kFCxkEO',
    'ADMIN',
    'ACTIVE',
    FALSE
)
ON DUPLICATE KEY UPDATE updated_at = CURRENT_TIMESTAMP;

-- 4. Add composite index for OTP lookups (user + unexpired + unused)
ALTER TABLE otp_tokens
    ADD INDEX idx_otp_user_active (user_id, expires_at, is_used);

-- 5. Add user_agent column to audit_logs
ALTER TABLE audit_logs
    ADD COLUMN user_agent VARCHAR(500) AFTER ip_address;
