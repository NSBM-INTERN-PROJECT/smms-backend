-- SMMS init-db.sql
-- Idempotent initialization script for local MySQL container
-- Creates service schemas and  core tables (no destructive operations)
-- Run by Docker entrypoint: /docker-entrypoint-initdb.d/init-db.sql

SET @@sql_mode=REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY','');
SET time_zone = '+00:00';

-- ==== Create databases (schemas) ====
CREATE DATABASE IF NOT EXISTS `smms_auth` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `smms_users` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `smms_alloc` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `smms_meeting` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `smms_session` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- ==================================================================
-- smms_auth schema
-- ==================================================================
USE `smms_auth`;

CREATE TABLE IF NOT EXISTS users (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	username VARCHAR(50) NOT NULL UNIQUE,
	email VARCHAR(100) NOT NULL UNIQUE,
	password_hash VARCHAR(255) NOT NULL,
	role ENUM('ADMIN','COORDINATOR','MENTOR','STUDENT','MANAGEMENT') NOT NULL,
	is_active BOOLEAN NOT NULL DEFAULT TRUE,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS refresh_tokens (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	user_id BIGINT NOT NULL,
	token VARCHAR(512) NOT NULL UNIQUE,
	expiry_date TIMESTAMP NOT NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	INDEX idx_token (token)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS audit_logs (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	user_id BIGINT NOT NULL,
	action VARCHAR(100) NOT NULL,
	entity_type VARCHAR(50),
	entity_id BIGINT,
	old_value TEXT,
	new_value TEXT,
	ip_address VARCHAR(45),
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	INDEX idx_audit_user (user_id),
	INDEX idx_audit_created (created_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS otp_tokens (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	user_id BIGINT NOT NULL,
	email VARCHAR(100) NOT NULL,
	otp_code VARCHAR(6) NOT NULL,
	expires_at TIMESTAMP NOT NULL,
	is_used BOOLEAN NOT NULL DEFAULT FALSE,
	attempts TINYINT NOT NULL DEFAULT 0,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	INDEX idx_otp_email (email),
	INDEX idx_otp_expires (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================================
-- smms_users schema
-- ==================================================================
USE `smms_users`;

CREATE TABLE IF NOT EXISTS mentor_profiles (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	user_id BIGINT NOT NULL UNIQUE,
	full_name VARCHAR(100) NOT NULL,
	employee_id VARCHAR(20) NOT NULL UNIQUE,
	department VARCHAR(100) NOT NULL,
	specialization VARCHAR(150),
	phone VARCHAR(20),
	max_students INT NOT NULL DEFAULT 5,
	is_active BOOLEAN NOT NULL DEFAULT TRUE,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_mentor_dept (department)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS student_profiles (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	user_id BIGINT NOT NULL UNIQUE,
	full_name VARCHAR(100) NOT NULL,
	student_id VARCHAR(20) NOT NULL UNIQUE,
	email VARCHAR(100) NOT NULL,
	phone VARCHAR(20),
	degree_program VARCHAR(100) NOT NULL,
	department VARCHAR(100) NOT NULL,
	batch VARCHAR(10) NOT NULL,
	intake VARCHAR(20) NOT NULL,
	academic_year TINYINT NOT NULL,
	risk_status ENUM('LOW','MEDIUM','HIGH','CRITICAL') NOT NULL DEFAULT 'LOW',
	is_active BOOLEAN NOT NULL DEFAULT TRUE,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_student_batch (batch),
	INDEX idx_student_degree (degree_program),
	INDEX idx_student_dept (department),
	INDEX idx_student_risk (risk_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS student_extended_profiles (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	student_user_id BIGINT NOT NULL UNIQUE,
	parent_name VARCHAR(100),
	parent_phone VARCHAR(20),
	parent_email VARCHAR(100),
	home_district VARCHAR(100),
	residence_address TEXT,
	emergency_contact_name VARCHAR(100),
	emergency_contact_phone VARCHAR(20),
	form_submitted BOOLEAN NOT NULL DEFAULT FALSE,
	submitted_at TIMESTAMP NULL,
	last_updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_ext_profile_student (student_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS profile_update_requests (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	student_user_id BIGINT NOT NULL,
	mentor_user_id BIGINT NOT NULL,
	field_name VARCHAR(100) NOT NULL,
	old_value TEXT,
	new_value TEXT NOT NULL,
	status ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
	mentor_notes VARCHAR(500),
	reviewed_at TIMESTAMP NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	INDEX idx_profile_req_student (student_user_id),
	INDEX idx_profile_req_mentor (mentor_user_id),
	INDEX idx_profile_req_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS data_collection_requests (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	mentor_user_id BIGINT NOT NULL,
	filter_criteria JSON NOT NULL,
	message TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	INDEX idx_dcr_mentor (mentor_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS data_collection_recipients (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	request_id BIGINT NOT NULL,
	student_user_id BIGINT NOT NULL,
	status ENUM('PENDING','SUBMITTED') NOT NULL DEFAULT 'PENDING',
	responded_at TIMESTAMP NULL,
	UNIQUE KEY uq_dcr_recipient (request_id, student_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================================
-- smms_alloc schema
-- ==================================================================
USE `smms_alloc`;

CREATE TABLE IF NOT EXISTS allocations (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	mentor_user_id BIGINT NOT NULL,
	student_user_id BIGINT NOT NULL,
	coordinator_user_id BIGINT NOT NULL,
	allocation_type ENUM('MANUAL','RANDOM') NOT NULL DEFAULT 'MANUAL',
	status ENUM('ACTIVE','INACTIVE','TRANSFERRED') NOT NULL DEFAULT 'ACTIVE',
	allocated_date DATE NOT NULL,
	deactivated_date DATE NULL,
	notes TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	UNIQUE KEY uq_active_student (student_user_id, status),
	INDEX idx_alloc_mentor (mentor_user_id),
	INDEX idx_alloc_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================================
-- smms_meeting schema
-- ==================================================================
USE `smms_meeting`;

CREATE TABLE IF NOT EXISTS meetings (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	allocation_id BIGINT NOT NULL,
	mentor_user_id BIGINT NOT NULL,
	student_user_id BIGINT NOT NULL,
	title VARCHAR(200) NOT NULL,
	description TEXT,
	scheduled_date DATE NOT NULL,
	scheduled_time TIME NOT NULL,
	duration_minutes INT NOT NULL DEFAULT 30,
	location VARCHAR(200),
	mode ENUM('PHYSICAL','ONLINE','HYBRID') NOT NULL DEFAULT 'PHYSICAL',
	meeting_link VARCHAR(500),
	status ENUM('SCHEDULED','COMPLETED','CANCELLED','RESCHEDULED') NOT NULL DEFAULT 'SCHEDULED',
	attendance_status ENUM('PENDING','PRESENT','ABSENT','LATE','EXCUSED') NOT NULL DEFAULT 'PENDING',
	reminder_sent BOOLEAN NOT NULL DEFAULT FALSE,
	rescheduled_from_id BIGINT NULL,
	reschedule_count TINYINT NOT NULL DEFAULT 0,
	cancelled_reason TEXT,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_meeting_mentor (mentor_user_id),
	INDEX idx_meeting_student (student_user_id),
	INDEX idx_meeting_date (scheduled_date),
	INDEX idx_meeting_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS meeting_slots (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	mentor_user_id BIGINT NOT NULL,
	slot_date DATE NOT NULL,
	start_time TIME NOT NULL,
	duration_minutes INT NOT NULL DEFAULT 30,
	mode ENUM('PHYSICAL','ONLINE','HYBRID') NOT NULL DEFAULT 'PHYSICAL',
	location VARCHAR(200),
	meeting_link VARCHAR(500),
	status ENUM('OPEN','ALLOCATED','COMPLETED','CANCELLED') NOT NULL DEFAULT 'OPEN',
	filter_criteria JSON NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	INDEX idx_slot_mentor (mentor_user_id),
	INDEX idx_slot_date (slot_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS meeting_slot_allocations (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	slot_id BIGINT NOT NULL,
	student_user_id BIGINT NOT NULL,
	status ENUM('PENDING','CONFIRMED','RESCHEDULE_REQUESTED','CANCELLED') NOT NULL DEFAULT 'PENDING',
	student_response ENUM('ACCEPTED','RESCHEDULE_REQUESTED') NULL,
	reschedule_reason TEXT,
	responded_at TIMESTAMP NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	UNIQUE KEY uq_slot_student (slot_id, student_user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS meeting_requests (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	student_user_id BIGINT NOT NULL,
	mentor_user_id BIGINT NOT NULL,
	preferred_date DATE NOT NULL,
	preferred_time TIME NOT NULL,
	reason TEXT,
	status ENUM('PENDING','APPROVED','REJECTED','RESCHEDULED') NOT NULL DEFAULT 'PENDING',
	mentor_response_notes VARCHAR(500),
	responded_at TIMESTAMP NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	INDEX idx_mreq_student (student_user_id),
	INDEX idx_mreq_mentor (mentor_user_id),
	INDEX idx_mreq_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS notifications (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	recipient_user_id BIGINT NOT NULL,
	type ENUM('MEETING_REMINDER','MEETING_SCHEDULED','MEETING_CANCELLED','MEETING_RESCHEDULED','SLOT_ASSIGNED','SLOT_RESPONSE','MEETING_REQUEST','ESCALATION_ALERT','ALLOCATION_NOTICE','FORM_REQUEST','FORM_SUBMITTED','PROFILE_UPDATE_REQUEST','PROFILE_UPDATE_RESULT','SYSTEM_ALERT') NOT NULL,
	title VARCHAR(200) NOT NULL,
	message TEXT NOT NULL,
	reference_id BIGINT NULL,
	reference_type VARCHAR(50) NULL,
	is_read BOOLEAN NOT NULL DEFAULT FALSE,
	email_sent BOOLEAN NOT NULL DEFAULT FALSE,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	INDEX idx_notif_recipient (recipient_user_id),
	INDEX idx_notif_read (is_read)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- ==================================================================
-- smms_session schema
-- ==================================================================
USE `smms_session`;

CREATE TABLE IF NOT EXISTS session_notes (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	meeting_id BIGINT NOT NULL UNIQUE,
	mentor_user_id BIGINT NOT NULL,
	student_user_id BIGINT NOT NULL,
	discussion_notes TEXT NOT NULL,
	action_items TEXT,
	progress_status ENUM('ON_TRACK','NEEDS_ATTENTION','AT_RISK','CRITICAL') NOT NULL DEFAULT 'ON_TRACK',
	follow_up_date DATE NULL,
	is_private BOOLEAN NOT NULL DEFAULT FALSE,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_session_student (student_user_id),
	INDEX idx_session_status (progress_status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE IF NOT EXISTS escalations (
	id BIGINT PRIMARY KEY AUTO_INCREMENT,
	session_note_id BIGINT NOT NULL,
	mentor_user_id BIGINT NOT NULL,
	student_user_id BIGINT NOT NULL,
	category ENUM('ACADEMIC','PERSONAL','CAREER','FINANCIAL','ATTENDANCE','HEALTH','OTHER') NOT NULL,
	description TEXT NOT NULL,
	escalated_to_role ENUM('COORDINATOR','MANAGEMENT','STUDENT_SUPPORT') NOT NULL,
	escalated_to_user_id BIGINT NULL,
	status ENUM('OPEN','ACKNOWLEDGED','IN_PROGRESS','RESOLVED','CLOSED') NOT NULL DEFAULT 'OPEN',
	resolution_notes TEXT,
	resolved_at TIMESTAMP NULL,
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
	updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
	INDEX idx_esc_student (student_user_id),
	INDEX idx_esc_status (status),
	INDEX idx_esc_category (category)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- End of init-db.sql

