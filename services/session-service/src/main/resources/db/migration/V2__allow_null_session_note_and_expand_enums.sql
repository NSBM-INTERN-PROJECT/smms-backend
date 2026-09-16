-- Allow escalations without an initial session note and expand category and role enums
ALTER TABLE escalations MODIFY session_note_id BIGINT NULL;
ALTER TABLE escalations MODIFY category ENUM('ACADEMIC','PERSONAL','CAREER','FINANCIAL','ATTENDANCE','HEALTH','OTHER','DISCIPLINARY','WELLBEING') NOT NULL;
ALTER TABLE escalations MODIFY escalated_to_role ENUM('COORDINATOR','MANAGEMENT','STUDENT_SUPPORT','HEAD_OF_DEPARTMENT','DEAN','COUNSELOR','ADMIN') NOT NULL;
