-- ─────────────────────────────────────────────────────────────
-- SMMS — Database Initialization Script
--
-- PURPOSE: Runs automatically on first MySQL Docker container start.
-- Creates all 5 schemas and grants the application user full access.
-- Subsequent runs are idempotent (IF NOT EXISTS).
--
-- This script runs as ROOT (the Docker entrypoint default).
-- The DB_ROOT_PASSWORD in docker-compose.yml authenticates this.
-- ─────────────────────────────────────────────────────────────

-- ─── CREATE SCHEMAS ───────────────────────────────────────────
CREATE DATABASE IF NOT EXISTS smms_auth
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS smms_users
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS smms_alloc
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS smms_meeting
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

CREATE DATABASE IF NOT EXISTS smms_session
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- ─── CREATE APPLICATION USER ──────────────────────────────────
-- The password here matches DB_PASS in your .env file.
-- Using '%' allows connections from any host (needed for Docker networking).
CREATE USER IF NOT EXISTS 'smms_user'@'%' IDENTIFIED BY 'Admin1234';

-- ─── GRANT PRIVILEGES ─────────────────────────────────────────
-- Each service only uses one schema, but we grant all schemas to one user
-- for simplicity in the zero-budget Docker Compose setup.
-- In production you would use separate users per schema.
GRANT ALL PRIVILEGES ON smms_auth.*    TO 'smms_user'@'%';
GRANT ALL PRIVILEGES ON smms_users.*   TO 'smms_user'@'%';
GRANT ALL PRIVILEGES ON smms_alloc.*   TO 'smms_user'@'%';
GRANT ALL PRIVILEGES ON smms_meeting.* TO 'smms_user'@'%';
GRANT ALL PRIVILEGES ON smms_session.* TO 'smms_user'@'%';

FLUSH PRIVILEGES;

-- ─── VERIFICATION ─────────────────────────────────────────────
-- After running: SHOW DATABASES; should include all 5 smms_* schemas.
-- SELECT User, Host FROM mysql.user; should include smms_user.