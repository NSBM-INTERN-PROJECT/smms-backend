-- Session Service — Full Schema (smms_session)
-- Flyway: V1__init_smms_session.sql
-- Add subsequent migrations as V2__... etc. NEVER modify this file.

CREATE TABLE IF NOT EXISTS session_notes (
    id               BIGINT    PRIMARY KEY AUTO_INCREMENT,
    meeting_id       BIGINT    NOT NULL UNIQUE,
    mentor_user_id   BIGINT    NOT NULL,
    student_user_id  BIGINT    NOT NULL,
    discussion_notes TEXT      NOT NULL,
    action_items     TEXT,
    progress_status  ENUM('ON_TRACK','NEEDS_ATTENTION','AT_RISK','CRITICAL') NOT NULL DEFAULT 'ON_TRACK',
    follow_up_date   DATE,
    is_private       BOOLEAN   NOT NULL DEFAULT FALSE,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_session_student (student_user_id),
    INDEX idx_session_status (progress_status)
);

CREATE TABLE IF NOT EXISTS escalations (
    id                   BIGINT    PRIMARY KEY AUTO_INCREMENT,
    session_note_id      BIGINT    NOT NULL,
    mentor_user_id       BIGINT    NOT NULL,
    student_user_id      BIGINT    NOT NULL,
    category             ENUM('ACADEMIC','PERSONAL','CAREER','FINANCIAL','ATTENDANCE','HEALTH','OTHER') NOT NULL,
    description          TEXT      NOT NULL,
    escalated_to_role    ENUM('COORDINATOR','MANAGEMENT','STUDENT_SUPPORT') NOT NULL,
    escalated_to_user_id BIGINT,
    status               ENUM('OPEN','ACKNOWLEDGED','IN_PROGRESS','RESOLVED','CLOSED') NOT NULL DEFAULT 'OPEN',
    resolution_notes     TEXT,
    resolved_at          TIMESTAMP,
    created_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at           TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_esc_student (student_user_id),
    INDEX idx_esc_status (status),
    INDEX idx_esc_category (category)
);
