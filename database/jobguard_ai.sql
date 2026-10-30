-- =====================================================================
-- JobGuard AI - Relational Database Schema
-- Database Name: jobguard_ai
-- Compatible with MySQL 8.0+ / MariaDB (Importable via phpMyAdmin)
-- =====================================================================

CREATE DATABASE IF NOT EXISTS `jobguard_ai` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE `jobguard_ai`;

SET FOREIGN_KEY_CHECKS = 0;
DROP TABLE IF EXISTS `courses`;
DROP TABLE IF EXISTS `placement_drives`;
DROP TABLE IF EXISTS `password_reset_tokens`;
DROP TABLE IF EXISTS `search_history`;
DROP TABLE IF EXISTS `ai_match_results`;
DROP TABLE IF EXISTS `notifications`;
DROP TABLE IF EXISTS `application_status_history`;
DROP TABLE IF EXISTS `job_applications`;
DROP TABLE IF EXISTS `saved_jobs`;
DROP TABLE IF EXISTS `jobs`;
DROP TABLE IF EXISTS `job_sources`;
DROP TABLE IF EXISTS `companies`;
DROP TABLE IF EXISTS `resume_analysis`;
DROP TABLE IF EXISTS `resumes`;
DROP TABLE IF EXISTS `user_preferences`;
DROP TABLE IF EXISTS `user_skills`;
DROP TABLE IF EXISTS `education_details`;
DROP TABLE IF EXISTS `user_profiles`;
DROP TABLE IF EXISTS `users`;
SET FOREIGN_KEY_CHECKS = 1;

-- ---------------------------------------------------------------------
-- 1. USERS TABLE
-- Core authentication entity storing credentials, status and verification.
-- ---------------------------------------------------------------------
CREATE TABLE `users` (
  `id` VARCHAR(64) NOT NULL,
  `full_name` VARCHAR(150) NOT NULL,
  `email` VARCHAR(191) NOT NULL,
  `phone` VARCHAR(30) DEFAULT NULL,
  `password_hash` VARCHAR(255) NOT NULL,
  `is_verified` TINYINT(1) NOT NULL DEFAULT 0,
  `verification_otp` VARCHAR(10) DEFAULT NULL,
  `otp_expires_at` DATETIME DEFAULT NULL,
  `auth_token` VARCHAR(255) DEFAULT NULL,
  `token_expires_at` DATETIME DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_users_email` (`email`),
  KEY `idx_users_email` (`email`),
  KEY `idx_users_auth_token` (`auth_token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 2. USER PROFILES TABLE
-- Extended jobseeker profile information.
-- ---------------------------------------------------------------------
CREATE TABLE `user_profiles` (
  `user_id` VARCHAR(64) NOT NULL,
  `headline` VARCHAR(255) DEFAULT NULL,
  `bio` TEXT DEFAULT NULL,
  `location` VARCHAR(150) DEFAULT NULL,
  `profile_image_url` VARCHAR(255) DEFAULT NULL,
  `profile_completion_pct` INT NOT NULL DEFAULT 20,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_user_profiles_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 3. EDUCATION DETAILS TABLE
-- Degrees, academic institutions, and grades.
-- ---------------------------------------------------------------------
CREATE TABLE `education_details` (
  `id` VARCHAR(64) NOT NULL,
  `user_id` VARCHAR(64) NOT NULL,
  `degree` VARCHAR(150) NOT NULL,
  `institution` VARCHAR(200) NOT NULL,
  `field_of_study` VARCHAR(150) DEFAULT NULL,
  `start_year` VARCHAR(20) DEFAULT NULL,
  `end_year` VARCHAR(20) DEFAULT NULL,
  `grade` VARCHAR(50) DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_education_user_id` (`user_id`),
  CONSTRAINT `fk_education_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 4. USER SKILLS TABLE
-- Individual normalized technical and soft skills.
-- ---------------------------------------------------------------------
CREATE TABLE `user_skills` (
  `id` INT AUTO_INCREMENT NOT NULL,
  `user_id` VARCHAR(64) NOT NULL,
  `skill_name` VARCHAR(100) NOT NULL,
  `proficiency_level` VARCHAR(50) DEFAULT 'Intermediate',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_skill` (`user_id`, `skill_name`),
  KEY `idx_skills_user_id` (`user_id`),
  KEY `idx_skill_name` (`skill_name`),
  CONSTRAINT `fk_skills_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 5. USER PREFERENCES TABLE
-- Job hunting criteria, desired roles, locations, and employer consent.
-- ---------------------------------------------------------------------
CREATE TABLE `user_preferences` (
  `id` INT AUTO_INCREMENT NOT NULL,
  `user_id` VARCHAR(64) NOT NULL,
  `preferred_roles` TEXT DEFAULT NULL, -- JSON array or comma separated
  `preferred_locations` TEXT DEFAULT NULL,
  `preferred_types` VARCHAR(100) DEFAULT 'Full-Time, Remote',
  `expected_salary` VARCHAR(100) DEFAULT NULL,
  `consent_shared_with_companies` TINYINT(1) NOT NULL DEFAULT 1,
  `notifications_enabled` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_preferences_user` (`user_id`),
  CONSTRAINT `fk_preferences_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 6. RESUMES TABLE
-- Stores uploaded resume files (PDF/DOCX) and parsed content.
-- ---------------------------------------------------------------------
CREATE TABLE `resumes` (
  `id` VARCHAR(64) NOT NULL,
  `user_id` VARCHAR(64) NOT NULL,
  `file_name` VARCHAR(255) NOT NULL,
  `file_path` VARCHAR(255) NOT NULL,
  `file_size` VARCHAR(50) NOT NULL DEFAULT '1.0 MB',
  `file_type` VARCHAR(50) NOT NULL DEFAULT 'application/pdf',
  `parsed_text` MEDIUMTEXT DEFAULT NULL,
  `is_default` TINYINT(1) NOT NULL DEFAULT 1,
  `upload_date` VARCHAR(50) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_resumes_user_id` (`user_id`),
  CONSTRAINT `fk_resumes_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 7. RESUME ANALYSIS TABLE
-- AI evaluation results, scoring, extracted sections and recommendations.
-- ---------------------------------------------------------------------
CREATE TABLE `resume_analysis` (
  `id` VARCHAR(64) NOT NULL,
  `resume_id` VARCHAR(64) NOT NULL,
  `user_id` VARCHAR(64) NOT NULL,
  `overall_score` INT NOT NULL DEFAULT 85,
  `extracted_skills` TEXT DEFAULT NULL, -- JSON array
  `extracted_education` TEXT DEFAULT NULL, -- JSON array
  `extracted_experience` TEXT DEFAULT NULL, -- JSON array
  `missing_sections` TEXT DEFAULT NULL, -- JSON array
  `improvement_suggestions` TEXT DEFAULT NULL, -- JSON array
  `analyzed_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_analysis_resume` (`resume_id`),
  KEY `idx_analysis_user_id` (`user_id`),
  CONSTRAINT `fk_analysis_resume` FOREIGN KEY (`resume_id`) REFERENCES `resumes` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_analysis_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 8. COMPANIES TABLE
-- Employer entities offering vacancies and placement drives.
-- ---------------------------------------------------------------------
CREATE TABLE `companies` (
  `id` VARCHAR(64) NOT NULL,
  `name` VARCHAR(150) NOT NULL,
  `logo_url` VARCHAR(255) DEFAULT '',
  `location` VARCHAR(150) NOT NULL,
  `industry` VARCHAR(100) NOT NULL,
  `description` TEXT NOT NULL,
  `website` VARCHAR(255) NOT NULL,
  `rating` DECIMAL(3,2) NOT NULL DEFAULT 4.50,
  `employee_count` VARCHAR(50) NOT NULL DEFAULT '100-500',
  `open_positions_count` INT NOT NULL DEFAULT 0,
  `is_verified` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_companies_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 9. JOB SOURCES TABLE
-- Feeds & APIs aggregating authentic vacancies (Arbeitnow, Lever, etc.).
-- ---------------------------------------------------------------------
CREATE TABLE `job_sources` (
  `id` VARCHAR(64) NOT NULL,
  `source_name` VARCHAR(100) NOT NULL,
  `api_endpoint` VARCHAR(255) DEFAULT NULL,
  `status` VARCHAR(50) NOT NULL DEFAULT 'active',
  `last_synced_at` DATETIME DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 10. JOBS TABLE
-- Real job listings with scam detection flags and AI matching attributes.
-- ---------------------------------------------------------------------
CREATE TABLE `jobs` (
  `id` VARCHAR(64) NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `company_id` VARCHAR(64) DEFAULT NULL,
  `company_name` VARCHAR(150) NOT NULL,
  `company_logo` VARCHAR(255) DEFAULT '',
  `location` VARCHAR(150) NOT NULL,
  `type` VARCHAR(50) NOT NULL DEFAULT 'Full-Time',
  `salary` VARCHAR(100) NOT NULL DEFAULT 'Competitive',
  `experience` VARCHAR(50) NOT NULL DEFAULT 'Not Specified',
  `description` TEXT NOT NULL,
  `requirements` TEXT DEFAULT NULL, -- JSON array of strings
  `skills` TEXT DEFAULT NULL, -- JSON array of strings
  `deadline` VARCHAR(50) DEFAULT '',
  `is_saved` TINYINT(1) NOT NULL DEFAULT 0,
  `is_scam_flagged` TINYINT(1) NOT NULL DEFAULT 0,
  `scam_reason` TEXT DEFAULT NULL,
  `match_score` INT NOT NULL DEFAULT 80,
  `source_id` VARCHAR(64) DEFAULT 'manual',
  `external_url` VARCHAR(500) DEFAULT '',
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_jobs_company_id` (`company_id`),
  KEY `idx_jobs_title` (`title`),
  KEY `idx_jobs_location` (`location`),
  KEY `idx_jobs_type` (`type`),
  KEY `idx_jobs_is_scam` (`is_scam_flagged`),
  KEY `idx_jobs_created_at` (`created_at`),
  CONSTRAINT `fk_jobs_company` FOREIGN KEY (`company_id`) REFERENCES `companies` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 11. SAVED JOBS TABLE
-- Tracks user bookmarks; prevents duplicates per user.
-- ---------------------------------------------------------------------
CREATE TABLE `saved_jobs` (
  `id` INT AUTO_INCREMENT NOT NULL,
  `user_id` VARCHAR(64) NOT NULL,
  `job_id` VARCHAR(64) NOT NULL,
  `saved_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_saved_job_user` (`user_id`, `job_id`),
  KEY `idx_saved_jobs_user_id` (`user_id`),
  KEY `idx_saved_jobs_job_id` (`job_id`),
  CONSTRAINT `fk_saved_jobs_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_saved_jobs_job` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 12. JOB APPLICATIONS TABLE
-- Records verified user submissions with status lifecycle.
-- ---------------------------------------------------------------------
CREATE TABLE `job_applications` (
  `id` VARCHAR(64) NOT NULL,
  `user_id` VARCHAR(64) NOT NULL,
  `job_id` VARCHAR(64) NOT NULL,
  `resume_id` VARCHAR(64) DEFAULT NULL,
  `cover_note` TEXT DEFAULT NULL,
  `status` VARCHAR(50) NOT NULL DEFAULT 'Submitted',
  `applied_date` VARCHAR(50) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_application_user_job` (`user_id`, `job_id`),
  KEY `idx_applications_user_id` (`user_id`),
  KEY `idx_applications_job_id` (`job_id`),
  KEY `idx_applications_status` (`status`),
  CONSTRAINT `fk_applications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_applications_job` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_applications_resume` FOREIGN KEY (`resume_id`) REFERENCES `resumes` (`id`) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 13. APPLICATION STATUS HISTORY TABLE
-- Audit log of interview milestones and reviews.
-- ---------------------------------------------------------------------
CREATE TABLE `application_status_history` (
  `id` INT AUTO_INCREMENT NOT NULL,
  `application_id` VARCHAR(64) NOT NULL,
  `status` VARCHAR(50) NOT NULL,
  `notes` TEXT DEFAULT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_status_history_application_id` (`application_id`),
  CONSTRAINT `fk_history_application` FOREIGN KEY (`application_id`) REFERENCES `job_applications` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 14. NOTIFICATIONS TABLE
-- Real-time notifications for status changes, new matches, and reminders.
-- ---------------------------------------------------------------------
CREATE TABLE `notifications` (
  `id` VARCHAR(64) NOT NULL,
  `user_id` VARCHAR(64) NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `message` TEXT NOT NULL,
  `type` VARCHAR(50) NOT NULL DEFAULT 'JobAlert',
  `is_read` TINYINT(1) NOT NULL DEFAULT 0,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_notifications_user_id` (`user_id`),
  KEY `idx_notifications_is_read` (`is_read`),
  CONSTRAINT `fk_notifications_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 15. AI MATCH RESULTS TABLE
-- Cached mathematical match scores between candidate profiles & jobs.
-- ---------------------------------------------------------------------
CREATE TABLE `ai_match_results` (
  `id` INT AUTO_INCREMENT NOT NULL,
  `user_id` VARCHAR(64) NOT NULL,
  `job_id` VARCHAR(64) NOT NULL,
  `match_score` INT NOT NULL,
  `matching_skills` TEXT DEFAULT NULL, -- JSON array
  `missing_skills` TEXT DEFAULT NULL,  -- JSON array
  `explanation` TEXT DEFAULT NULL,
  `calculated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_match_user_job` (`user_id`, `job_id`),
  KEY `idx_match_user` (`user_id`),
  KEY `idx_match_job` (`job_id`),
  CONSTRAINT `fk_match_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_match_job` FOREIGN KEY (`job_id`) REFERENCES `jobs` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 16. SEARCH HISTORY TABLE
-- User search keywords and applied filters.
-- ---------------------------------------------------------------------
CREATE TABLE `search_history` (
  `id` INT AUTO_INCREMENT NOT NULL,
  `user_id` VARCHAR(64) NOT NULL,
  `query` VARCHAR(255) NOT NULL,
  `filters_json` TEXT DEFAULT NULL,
  `searched_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_search_history_user` (`user_id`),
  CONSTRAINT `fk_search_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 17. PASSWORD RESET TOKENS TABLE
-- Secure expiring tokens for password recovery.
-- ---------------------------------------------------------------------
CREATE TABLE `password_reset_tokens` (
  `id` INT AUTO_INCREMENT NOT NULL,
  `email` VARCHAR(191) NOT NULL,
  `token` VARCHAR(128) NOT NULL,
  `expires_at` DATETIME NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_reset_email` (`email`),
  KEY `idx_reset_token` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 18. PLACEMENT DRIVES TABLE
-- Campus and virtual recruitment drives for students and graduates.
-- ---------------------------------------------------------------------
CREATE TABLE `placement_drives` (
  `id` VARCHAR(64) NOT NULL,
  `company_name` VARCHAR(150) NOT NULL,
  `company_logo` VARCHAR(255) DEFAULT '',
  `drive_title` VARCHAR(200) NOT NULL,
  `role` VARCHAR(150) NOT NULL,
  `package_amount` VARCHAR(100) NOT NULL,
  `eligibility_criteria` TEXT NOT NULL,
  `drive_date` VARCHAR(50) NOT NULL,
  `location` VARCHAR(150) NOT NULL,
  `status` VARCHAR(50) NOT NULL DEFAULT 'Upcoming',
  `description` TEXT NOT NULL,
  `rounds` TEXT DEFAULT NULL, -- JSON array
  `registration_deadline` VARCHAR(50) NOT NULL,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- 19. COURSES TABLE
-- Recommended upskilling courses for skill gap closure and learning roadmaps.
-- ---------------------------------------------------------------------
CREATE TABLE `courses` (
  `id` VARCHAR(64) NOT NULL,
  `title` VARCHAR(200) NOT NULL,
  `provider` VARCHAR(150) NOT NULL,
  `duration` VARCHAR(50) NOT NULL,
  `level` VARCHAR(50) NOT NULL DEFAULT 'Intermediate',
  `skill_category` VARCHAR(100) NOT NULL,
  `description` TEXT NOT NULL,
  `url` VARCHAR(255) DEFAULT '',
  `modules` TEXT DEFAULT NULL, -- JSON array
  `is_active` TINYINT(1) NOT NULL DEFAULT 1,
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_courses_skill_category` (`skill_category`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ---------------------------------------------------------------------
-- INITIAL SEED DATA
-- Legitimate baseline data for companies, verified placements & learning courses
-- ---------------------------------------------------------------------
INSERT INTO `companies` (`id`, `name`, `logo_url`, `location`, `industry`, `description`, `website`, `rating`, `employee_count`, `open_positions_count`, `is_verified`) VALUES
('comp_1', 'TechCorp Solutions', '', 'San Francisco, CA', 'Software & AI Solutions', 'TechCorp Solutions is a global leader in cloud and mobile technology solutions powering Fortune 500 enterprises with cutting edge AI products.', 'https://techcorp-solutions.example.com', 4.80, '1,000-5,000', 14, 1),
('comp_2', 'InnovateX Labs', '', 'Remote / Palo Alto, CA', 'NextGen Mobile Technology', 'InnovateX Labs creates intuitive mobile tools and design frameworks used by millions of developers worldwide.', 'https://innovatex.example.com', 4.60, '250-500', 8, 1),
('comp_3', 'CyberGenesis Inc.', '', 'New York, NY', 'Cybersecurity & On-Device AI', 'CyberGenesis builds high-assurance mobile security and edge AI systems.', 'https://cybergenesis.example.com', 4.70, '500-1,000', 5, 1);

INSERT INTO `placement_drives` (`id`, `company_name`, `company_logo`, `drive_title`, `role`, `package_amount`, `eligibility_criteria`, `drive_date`, `location`, `status`, `description`, `rounds`, `registration_deadline`) VALUES
('place_1', 'Microsoft Placement Drive 2025', '', 'Campus Placement Drive - Software Engineer', 'Software Development Engineer (SDE-1)', '$120,000 / annum', 'CGPA 7.5+ in B.Tech / M.Tech CS/IT (Batch 2025/2026)', '15 Nov 2025', 'On-Campus & Virtual', 'Ongoing', 'Microsoft Campus Recruitment Drive for final year software engineering students. Multiple roles available in Mobile, Cloud, and Systems.', '[\"Aptitude & Coding Assessment\", \"Technical Interview Round 1\", \"Technical Interview Round 2\", \"HR & Culture Round\"]', '10 Nov 2025'),
('place_2', 'Google University Recruitment', '', 'Associate Android Developer Drive', 'Associate Mobile Software Engineer', '$135,000 / annum', 'B.E / B.Tech / B.Sc CS graduates with strong DSA and Kotlin knowledge', '22 Nov 2025', 'Virtual Drive', 'Upcoming', 'Google is hiring upcoming graduates for Android ecosystem teams. Candidates will work on Android OS components and Google Play services.', '[\"Online Coding Challenge (2 Hours)\", \"System Design & DSA Interview\", \"Googleiness Leadership Round\"]', '18 Nov 2025');

INSERT INTO `courses` (`id`, `title`, `provider`, `duration`, `level`, `skill_category`, `description`, `url`, `modules`, `is_active`) VALUES
('course_1', 'Mastering Jetpack Compose UI', 'JobGuard AI Academy', '6 Hours', 'Intermediate', 'Jetpack Compose', 'Deep dive into Compose layouts, animations, state management, and custom canvas drawing.', 'https://developer.android.com/jetpack/compose', '[\"Compose Basics & Modifiers\", \"State Management with StateFlow\", \"Navigation & Deep Linking\", \"Performance Optimization\"]', 1),
('course_2', 'Kotlin Coroutines & Flow in Depth', 'JobGuard AI Academy', '4 Hours', 'Advanced', 'Coroutines', 'Comprehensive guide to asynchronous structured concurrency, exception handling, and reactive data streams.', 'https://kotlinlang.org/docs/coroutines-overview.html', '[\"Structured Concurrency\", \"Channels & StateFlow\", \"Exception Handling Strategies\", \"Unit Testing Coroutines\"]', 1),
('course_3', 'Clean MVVM & Architecture Patterns', 'JobGuard AI Academy', '5 Hours', 'Intermediate', 'MVVM', 'Architect resilient, scalable native Android applications conforming to Google clean architecture guidelines.', 'https://developer.android.com/topic/architecture', '[\"Repository Pattern\", \"UseCase Layer\", \"Offline-First Room Sync\", \"Unit & Instrumentation Testing\"]', 1);

INSERT INTO `job_sources` (`id`, `source_name`, `api_endpoint`, `status`, `last_synced_at`) VALUES
('source_arbeitnow', 'Arbeitnow API', 'https://www.arbeitnow.com/api/job-board-api', 'active', NOW()),
('source_manual', 'Verified Employers', NULL, 'active', NOW());

