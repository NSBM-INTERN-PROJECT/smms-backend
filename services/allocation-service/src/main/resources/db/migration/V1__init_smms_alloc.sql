-- Allocation Service — Full Schema (smms_alloc)
-- Flyway: V1__init_smms_alloc.sql
-- Add subsequent migrations as V2__... etc. NEVER modify this file.

CREATE TABLE IF NOT EXISTS allocations (
    id                  BIGINT    PRIMARY KEY AUTO_INCREMENT,
    mentor_user_id      BIGINT    NOT NULL,
    student_user_id     BIGINT    NOT NULL,
    coordinator_user_id BIGINT    NOT NULL,
    allocation_type     ENUM('MANUAL','RANDOM') NOT NULL DEFAULT 'MANUAL',
    status              ENUM('ACTIVE','INACTIVE','TRANSFERRED') NOT NULL DEFAULT 'ACTIVE',
    allocated_date      DATE      NOT NULL,
    deactivated_date    DATE,
    notes               TEXT,
    created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uq_active_student (student_user_id, status),
    INDEX idx_alloc_mentor (mentor_user_id),
    INDEX idx_alloc_status (status)
);
