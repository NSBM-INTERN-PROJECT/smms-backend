-- User Service — Full Schema (smms_users)
-- Flyway: V1__init_smms_users.sql
-- Add subsequent migrations as V2__..., V3__... etc. NEVER modify this file.

CREATE TABLE IF NOT EXISTS mentor_profiles (
    id             BIGINT       PRIMARY KEY AUTO_INCREMENT,
    user_id        BIGINT       NOT NULL UNIQUE,
    full_name      VARCHAR(100) NOT NULL,
    employee_id    VARCHAR(20)  NOT NULL UNIQUE,
    department     VARCHAR(100) NOT NULL,
    specialization VARCHAR(150),
    phone          VARCHAR(20),
    max_students   INT          NOT NULL DEFAULT 5,
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_mentor_dept (department)
);

CREATE TABLE IF NOT EXISTS student_profiles (
    id             BIGINT       PRIMARY KEY AUTO_INCREMENT,
    user_id        BIGINT       NOT NULL UNIQUE,
    full_name      VARCHAR(100) NOT NULL,
    student_id     VARCHAR(20)  NOT NULL UNIQUE,
    email          VARCHAR(100) NOT NULL,
    phone          VARCHAR(20),
    degree_program VARCHAR(100) NOT NULL,
    department     VARCHAR(100) NOT NULL,
    batch          VARCHAR(10)  NOT NULL,
    intake         VARCHAR(20)  NOT NULL,
    academic_year  TINYINT      NOT NULL,
    risk_status    ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL DEFAULT 'LOW',
    is_active      BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_student_batch (batch),
    INDEX idx_student_degree (degree_program),
    INDEX idx_student_dept (department),
    INDEX idx_student_risk (risk_status)
);

CREATE TABLE IF NOT EXISTS student_extended_profiles (
    id                      BIGINT       PRIMARY KEY AUTO_INCREMENT,
    student_user_id         BIGINT       NOT NULL UNIQUE,
    parent_name             VARCHAR(100),
    parent_phone            VARCHAR(20),
    parent_email            VARCHAR(100),
    home_district           VARCHAR(100),
    residence_address       TEXT,
    emergency_contact_name  VARCHAR(100),
    emergency_contact_phone VARCHAR(20),
    form_submitted          BOOLEAN      NOT NULL DEFAULT FALSE,
    submitted_at            TIMESTAMP,
    last_updated_at         TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_ext_profile_student (student_user_id)
);

CREATE TABLE IF NOT EXISTS profile_update_requests (
    id               BIGINT       PRIMARY KEY AUTO_INCREMENT,
    student_user_id  BIGINT       NOT NULL,
    mentor_user_id   BIGINT       NOT NULL,
    field_name       VARCHAR(100) NOT NULL,
    old_value        TEXT,
    new_value        TEXT         NOT NULL,
    status           ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    mentor_notes     VARCHAR(500),
    reviewed_at      TIMESTAMP,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_profile_req_student (student_user_id),
    INDEX idx_profile_req_mentor (mentor_user_id),
    INDEX idx_profile_req_status (status)
);

CREATE TABLE IF NOT EXISTS data_collection_requests (
    id               BIGINT    PRIMARY KEY AUTO_INCREMENT,
    mentor_user_id   BIGINT    NOT NULL,
    filter_criteria  JSON      NOT NULL,
    message          TEXT,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_dcr_mentor (mentor_user_id)
);

CREATE TABLE IF NOT EXISTS data_collection_recipients (
    id               BIGINT    PRIMARY KEY AUTO_INCREMENT,
    request_id       BIGINT    NOT NULL,
    student_user_id  BIGINT    NOT NULL,
    status           ENUM('PENDING','SUBMITTED') NOT NULL DEFAULT 'PENDING',
    responded_at     TIMESTAMP,
    UNIQUE KEY uq_dcr_recipient (request_id, student_user_id)
);
