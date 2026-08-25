-- ─────────────────────────────────────────────────────────────
-- Auth Service — Initial Schema Migration
-- Schema: smms_auth
-- Flyway: V1__init_smms_auth.sql
--
-- Add subsequent migrations as V2__..., V3__... etc.
-- NEVER modify this file after it has been applied.
-- ─────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS users (
    id            BIGINT       PRIMARY KEY AUTO_INCREMENT,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role          ENUM('ADMIN','COORDINATOR','MENTOR','STUDENT','MANAGEMENT') NOT NULL,
    is_active     BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at    TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS refresh_tokens (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    token       VARCHAR(512) NOT NULL UNIQUE,
    expiry_date TIMESTAMP    NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_refresh_token (token),
    INDEX idx_refresh_user (user_id)
);

CREATE TABLE IF NOT EXISTS audit_logs (
    id          BIGINT       PRIMARY KEY AUTO_INCREMENT,
    user_id     BIGINT       NOT NULL,
    action      VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50),
    entity_id   BIGINT,
    old_value   TEXT,
    new_value   TEXT,
    ip_address  VARCHAR(45),
    created_at  TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_audit_user (user_id),
    INDEX idx_audit_created (created_at)
);

CREATE TABLE IF NOT EXISTS otp_tokens (
    id         BIGINT      PRIMARY KEY AUTO_INCREMENT,
    user_id    BIGINT      NOT NULL,
    email      VARCHAR(100) NOT NULL,
    otp_code   VARCHAR(6)  NOT NULL,
    expires_at TIMESTAMP   NOT NULL,
    is_used    BOOLEAN     NOT NULL DEFAULT FALSE,
    attempts   TINYINT     NOT NULL DEFAULT 0,
    created_at TIMESTAMP   NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_otp_email (email),
    INDEX idx_otp_expires (expires_at)
);
