-- Meeting Service — Full Schema (smms_meeting)
-- Flyway: V1__init_smms_meeting.sql
-- Add subsequent migrations as V2__... etc. NEVER modify this file.

CREATE TABLE IF NOT EXISTS meetings (
    id                  BIGINT       PRIMARY KEY AUTO_INCREMENT,
    allocation_id       BIGINT       NOT NULL,
    mentor_user_id      BIGINT       NOT NULL,
    student_user_id     BIGINT       NOT NULL,
    title               VARCHAR(200) NOT NULL,
    description         TEXT,
    scheduled_date      DATE         NOT NULL,
    scheduled_time      TIME         NOT NULL,
    duration_minutes    INT          NOT NULL DEFAULT 30,
    location            VARCHAR(200),
    mode                ENUM('PHYSICAL','ONLINE','HYBRID') NOT NULL DEFAULT 'PHYSICAL',
    meeting_link        VARCHAR(500),
    status              ENUM('SCHEDULED','COMPLETED','CANCELLED','RESCHEDULED') NOT NULL DEFAULT 'SCHEDULED',
    attendance_status   ENUM('PENDING','PRESENT','ABSENT','LATE','EXCUSED') NOT NULL DEFAULT 'PENDING',
    reminder_sent       BOOLEAN      NOT NULL DEFAULT FALSE,
    rescheduled_from_id BIGINT,
    reschedule_count    TINYINT      NOT NULL DEFAULT 0,
    cancelled_reason    TEXT,
    created_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_meeting_mentor (mentor_user_id),
    INDEX idx_meeting_student (student_user_id),
    INDEX idx_meeting_date (scheduled_date),
    INDEX idx_meeting_status (status)
);

CREATE TABLE IF NOT EXISTS meeting_slots (
    id               BIGINT       PRIMARY KEY AUTO_INCREMENT,
    mentor_user_id   BIGINT       NOT NULL,
    slot_date        DATE         NOT NULL,
    start_time       TIME         NOT NULL,
    duration_minutes INT          NOT NULL DEFAULT 30,
    mode             ENUM('PHYSICAL','ONLINE','HYBRID') NOT NULL DEFAULT 'PHYSICAL',
    location         VARCHAR(200),
    meeting_link     VARCHAR(500),
    status           ENUM('OPEN','ALLOCATED','COMPLETED','CANCELLED') NOT NULL DEFAULT 'OPEN',
    filter_criteria  JSON,
    created_at       TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_slot_mentor (mentor_user_id),
    INDEX idx_slot_date (slot_date)
);

CREATE TABLE IF NOT EXISTS meeting_slot_allocations (
    id               BIGINT    PRIMARY KEY AUTO_INCREMENT,
    slot_id          BIGINT    NOT NULL,
    student_user_id  BIGINT    NOT NULL,
    status           ENUM('PENDING','CONFIRMED','RESCHEDULE_REQUESTED','CANCELLED') NOT NULL DEFAULT 'PENDING',
    student_response ENUM('ACCEPTED','RESCHEDULE_REQUESTED'),
    reschedule_reason TEXT,
    responded_at     TIMESTAMP,
    created_at       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uq_slot_student (slot_id, student_user_id)
);

CREATE TABLE IF NOT EXISTS meeting_requests (
    id                    BIGINT       PRIMARY KEY AUTO_INCREMENT,
    student_user_id       BIGINT       NOT NULL,
    mentor_user_id        BIGINT       NOT NULL,
    preferred_date        DATE         NOT NULL,
    preferred_time        TIME         NOT NULL,
    reason                TEXT,
    status                ENUM('PENDING','APPROVED','REJECTED','RESCHEDULED') NOT NULL DEFAULT 'PENDING',
    mentor_response_notes VARCHAR(500),
    responded_at          TIMESTAMP,
    created_at            TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_mreq_student (student_user_id),
    INDEX idx_mreq_mentor (mentor_user_id),
    INDEX idx_mreq_status (status)
);

CREATE TABLE IF NOT EXISTS notifications (
    id                BIGINT       PRIMARY KEY AUTO_INCREMENT,
    recipient_user_id BIGINT       NOT NULL,
    type              ENUM('MEETING_REMINDER','MEETING_SCHEDULED','MEETING_CANCELLED',
                          'MEETING_RESCHEDULED','SLOT_ASSIGNED','SLOT_RESPONSE',
                          'MEETING_REQUEST','ESCALATION_ALERT','ALLOCATION_NOTICE',
                          'FORM_REQUEST','FORM_SUBMITTED','PROFILE_UPDATE_REQUEST',
                          'PROFILE_UPDATE_RESULT','SYSTEM_ALERT') NOT NULL,
    title             VARCHAR(200) NOT NULL,
    message           TEXT         NOT NULL,
    reference_id      BIGINT,
    reference_type    VARCHAR(50),
    is_read           BOOLEAN      NOT NULL DEFAULT FALSE,
    email_sent        BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_notif_recipient (recipient_user_id),
    INDEX idx_notif_read (is_read)
);
