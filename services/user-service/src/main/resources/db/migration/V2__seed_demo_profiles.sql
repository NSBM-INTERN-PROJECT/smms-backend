-- ─────────────────────────────────────────────────────────────
-- User Service — V2 Migration
-- Seeds demo profiles for mentors and students
-- ─────────────────────────────────────────────────────────────

INSERT INTO mentor_profiles (user_id, full_name, employee_id, department, specialization, phone, max_students, is_active)
VALUES 
    (18, 'Dr. Grace Hopper', 'EMP-2024-018', 'Software Engineering', 'Distributed Systems & Compiler Architecture', '+94 77 123 4567', 15, TRUE),
    (2,  'Prof. Ada Lovelace', 'EMP-2024-002', 'Software Engineering', 'Algorithms & Computational Theory', '+94 77 987 6543', 20, TRUE)
ON DUPLICATE KEY UPDATE 
    full_name = VALUES(full_name),
    is_active = TRUE;

INSERT INTO student_profiles (user_id, full_name, student_id, email, phone, degree_program, department, batch, intake, academic_year, risk_status, is_active)
VALUES 
    (42, 'John Doe', 'STU-2024-001', 'john.doe@smms.edu', '+94 71 234 5678', 'BSc (Hons) Software Engineering', 'Software Engineering', '2024', 'Spring', 2, 'LOW', TRUE)
ON DUPLICATE KEY UPDATE 
    full_name = VALUES(full_name),
    is_active = TRUE;

INSERT INTO student_extended_profiles (student_user_id, parent_name, parent_phone, parent_email, home_district, residence_address, emergency_contact_name, emergency_contact_phone, form_submitted)
VALUES 
    (42, 'Robert Doe', '+94 71 999 8888', 'robert.doe@example.com', 'Colombo', '124 Reid Avenue, Colombo 07', 'Mary Doe', '+94 71 888 7777', TRUE)
ON DUPLICATE KEY UPDATE 
    parent_name = VALUES(parent_name);
