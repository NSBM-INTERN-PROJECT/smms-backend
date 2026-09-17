-- ─────────────────────────────────────────────────────────────
-- Allocation Service — V2 Migration
-- Seeds initial allocation between Dr. Grace Hopper (18) and John Doe (42)
-- ─────────────────────────────────────────────────────────────

INSERT INTO allocations (id, mentor_user_id, student_user_id, coordinator_user_id, allocation_type, status, allocated_date)
VALUES 
    (1, 18, 42, 2, 'MANUAL', 'ACTIVE', '2024-01-15')
ON DUPLICATE KEY UPDATE 
    status = 'ACTIVE';
