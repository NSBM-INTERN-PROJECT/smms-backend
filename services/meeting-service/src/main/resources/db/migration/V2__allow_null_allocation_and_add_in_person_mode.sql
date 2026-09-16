-- Meeting Service Migration V2: Allow null allocation_id and add IN_PERSON to mode enum
ALTER TABLE meetings MODIFY allocation_id BIGINT NULL;
ALTER TABLE meetings MODIFY mode ENUM('PHYSICAL','IN_PERSON','ONLINE','HYBRID') NOT NULL DEFAULT 'PHYSICAL';
ALTER TABLE meeting_slots MODIFY mode ENUM('PHYSICAL','IN_PERSON','ONLINE','HYBRID') NOT NULL DEFAULT 'PHYSICAL';
