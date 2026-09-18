-- ─────────────────────────────────────────────────────────────
-- Auth Service — V3 Migration
-- Seeds demo institutional accounts for all 4 roles:
--   - Admin:       admin@smms.edu        / Password@123
--   - Coordinator: coordinator@smms.edu  / Password@123
--   - Mentor:      grace.hopper@smms.edu / Password@123
--   - Student:     john.doe@smms.edu     / Password@123
-- ─────────────────────────────────────────────────────────────

INSERT INTO users (id, username, email, full_name, password_hash, role, status, must_change_password)
VALUES 
    (1,  'admin',        'admin@smms.edu',        'Dr. Alan Turing',     '$2a$12$eRi0APfQakW5CqTKMD6wiug2UF7HAWYBUdOJjhjb.vTPhGFv5fTU.', 'ADMIN',       'ACTIVE', FALSE),
    (2,  'coordinator',  'coordinator@smms.edu',  'Prof. Ada Lovelace',  '$2a$12$eRi0APfQakW5CqTKMD6wiug2UF7HAWYBUdOJjhjb.vTPhGFv5fTU.', 'COORDINATOR', 'ACTIVE', FALSE),
    (18, 'grace_hopper', 'grace.hopper@smms.edu', 'Dr. Grace Hopper',    '$2a$12$eRi0APfQakW5CqTKMD6wiug2UF7HAWYBUdOJjhjb.vTPhGFv5fTU.', 'MENTOR',      'ACTIVE', FALSE),
    (42, 'john_doe',     'john.doe@smms.edu',     'John Doe',            '$2a$12$eRi0APfQakW5CqTKMD6wiug2UF7HAWYBUdOJjhjb.vTPhGFv5fTU.', 'STUDENT',     'ACTIVE', FALSE)
ON DUPLICATE KEY UPDATE 
    email = VALUES(email),
    password_hash = VALUES(password_hash),
    status = 'ACTIVE';
