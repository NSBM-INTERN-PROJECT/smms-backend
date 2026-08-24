-- SMMS init-db.sql
-- Idempotent initialization script for local MySQL container
-- Creates service schemas only and need to create core tables (no destructive operations)
-- Run by Docker entrypoint: /docker-entrypoint-initdb.d/init-db.sql

SET @@sql_mode=REPLACE(@@sql_mode,'ONLY_FULL_GROUP_BY','');
SET time_zone = '+00:00';

-- ==== Create databases (schemas) ====
CREATE DATABASE IF NOT EXISTS `smms_auth` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `smms_users` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `smms_alloc` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `smms_meeting` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE DATABASE IF NOT EXISTS `smms_session` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- End of init-db.sql

