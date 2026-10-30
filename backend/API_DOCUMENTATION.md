# JobGuard AI — Backend REST API & Database Integration Documentation

## 1. System Architecture & Environment

JobGuard AI connects a modern Android frontend (Kotlin, Jetpack Compose, Material 3, MVVM, Retrofit, StateFlow) to a secure PHP 8.0+ REST API backend backed by a relational MySQL (MariaDB) database running via XAMPP.

### Local Development Configuration
- **Web Server:** Apache (via XAMPP) listening on port `80`
- **Database Server:** MariaDB 10.4.32 (via XAMPP) listening on port `3307`
  - *Note:* Windows Service `MySQL80` occupies port `3306`. XAMPP is configured on port `3307` with user `root` and blank password (`""`).
- **Database Name:** `jobguard_ai` (19 relational tables)
- **Apache Directory Junction:** `C:\xampp\htdocs\jobguard_ai` -> `C:\Users\SCAD LAB\jobguard-ai`
- **Backend API Base URLs:**
  - Localhost (Browser / Postman / cURL): `http://localhost/jobguard_ai/backend/api/`
  - Android Emulator: `http://10.0.2.2/jobguard_ai/backend/api/`
  - Android Physical Device on Wi-Fi: `http://<YOUR_LAN_IP>/jobguard_ai/backend/api/`

---

## 2. MySQL Database Schema (`database/jobguard_ai.sql`)

The database consists of 19 normalized relational tables with full foreign keys, indexes, and constraints:
1. `users` — Primary account table (id, full_name, email, password_hash, phone, is_verified, created_at, updated_at).
2. `user_profiles` — Professional profile (headline, bio, location, years_experience, current_salary, expected_salary, notice_period).
3. `education_details` — Education degrees and institutions (degree, institution, field_of_study, start_year, end_year, grade_cgpa).
4. `user_skills` — Technical and professional skills with proficiency level and experience years.
5. `user_preferences` — Career preferences (desired roles, preferred locations, job types, remote allowed, scam alerts, consent).
6. `resumes` — Uploaded resumes metadata and storage path (file_name, file_path, file_size, mime_type, is_default).
7. `resume_analysis` — AI parsing results (ats_score, readiness_score, extracted_skills, extracted_experience, improvement_suggestions).
8. `companies` — Verified employer profiles (name, logo_url, industry, location, description, website, rating, employee_count).
9. `job_sources` — Ingested vacancy feeds (Arbeitnow, LinkedIn, Indeed, Internal placement drives).
10. `jobs` — Live vacancy listings (title, company_id, location, job_type, salary_range, description, requirements, scam_risk_score, is_scam_flagged, scam_reasons).
11. `saved_jobs` — User bookmarked jobs (unique composite key `user_id + job_id`).
12. `job_applications` — Submitted applications with status tracking (`submitted`, `under_review`, `shortlisted`, `rejected`, `hired`).
13. `application_status_history` — Audit trail for application status changes with timestamps.
14. `notifications` — In-app alerts, scam warnings, and application updates.
15. `ai_match_results` — Precomputed AI compatibility scores between resumes and job postings.
16. `search_history` — Search terms and filter criteria recorded for personalization.
17. `password_reset_tokens` — Secure single-use tokens with expiration for password recovery.
18. `placement_drives` — Campus & corporate placement drives with eligibility criteria and interview rounds.
19. `courses` — AI recommended upskilling courses mapped to skill categories.

---

## 3. REST API Endpoint Catalog

All requests and responses use `Content-Type: application/json`.
Protected endpoints require HTTP header: `Authorization: Bearer <AUTH_TOKEN>`.

### Authentication & Account APIs (`api/auth/`)
| Endpoint | Method | Auth Required | Description |
|---|---|---|---|
| `api/auth/register.php` | `POST` | No | Creates new account, hashes password using `PASSWORD_BCRYPT`, generates 4-digit OTP. |
| `api/auth/login.php` | `POST` | No | Verifies credentials with `password_verify()`, issues 30-day Bearer token. |
| `api/auth/verify_email.php` | `POST` | Optional | Validates OTP and activates user account. |
| `api/auth/resend_otp.php` | `POST` | Optional | Generates new OTP code with 15-minute expiration. |
| `api/auth/forgot_password.php` | `POST` | No | Generates cryptographically secure 64-char reset token. |
| `api/auth/reset_password.php` | `POST` | No | Resets password using valid unexpired token. |
| `api/auth/change_password.php` | `POST` | **Yes** | Updates user password after verifying current password. |
| `api/auth/me.php` | `GET` | **Yes** | Returns authenticated user account profile and completion %. |
| `api/auth/logout.php` | `POST` | **Yes** | Revokes active session token. |

### User Profile & Skills APIs (`api/profile/`, `api/education/`, `api/skills/`)
| Endpoint | Method | Auth Required | Description |
|---|---|---|---|
| `api/profile/get.php` | `GET` | **Yes** | Fetches full profile including education, skills, and preferences. |
| `api/profile/update.php` | `POST` | **Yes** | Updates headline, bio, location, and years of experience. |
| `api/profile/preferences.php`| `POST` | **Yes** | Saves desired roles, job types, and remote preferences. |
| `api/education/list.php` | `GET` | **Yes** | Lists all education entries for the user. |
| `api/education/save.php` | `POST` | **Yes** | Creates or updates an education entry. |
| `api/education/delete.php` | `POST` | **Yes** | Removes an education entry by ID. |
| `api/skills/list.php` | `GET` | **Yes** | Returns user's saved skills. |
| `api/skills/update.php` | `POST` | **Yes** | Replaces user's skills with an updated list of skill tags. |
| `api/skills/available.php` | `GET` | No | Returns available skills catalog for autocomplete. |

### Real Job Vacancies & Aggregation APIs (`api/jobs/`, `api/companies/`, `api/placements/`)
| Endpoint | Method | Auth Required | Description |
|---|---|---|---|
| `api/jobs/sync.php` | `POST` | No | Pulls live job vacancies from Arbeitnow API, inspects scam risk, seeds MySQL. |
| `api/jobs/search.php` | `GET` | Optional | Searches jobs by title, company, skills, location, type. |
| `api/jobs/detail.php` | `GET` | Optional | Returns full job description, scam analysis breakdown, and save status. |
| `api/jobs/recommendations.php`| `GET` | **Yes** | AI matching of user skills against active vacancies. |
| `api/jobs/filters_meta.php` | `GET` | No | Returns dynamic filter options (types, levels, locations). |
| `api/companies/detail.php` | `GET` | Optional | Returns company overview, ratings, open positions count. |
| `api/placements/list.php` | `GET` | Optional | Lists upcoming college and corporate placement drives. |
| `api/placements/detail.php` | `GET` | Optional | Fetches placement drive rounds, criteria, and package details. |

### Resume Upload & AI Analysis APIs (`api/resume/`, `api/ai/`, `api/courses/`)
| Endpoint | Method | Auth Required | Description |
|---|---|---|---|
| `api/resume/upload.php` | `POST` | **Yes** | Accepts multipart PDF/DOCX, extracts text, computes ATS score, generates suggestions. |
| `api/resume/default.php` | `GET` | **Yes** | Returns user's active resume and AI analysis report. |
| `api/ai/skill_gap.php` | `GET` | **Yes** | Analyzes user skills vs target role benchmark and recommends courses. |
| `api/courses/detail.php` | `GET` | Optional | Returns course syllabus, modules, and learning outcomes. |

### Saved Jobs, Applications & Dashboard APIs (`api/saved_jobs/`, `api/applications/`, `api/dashboard/`, `api/notifications/`)
| Endpoint | Method | Auth Required | Description |
|---|---|---|---|
| `api/saved_jobs/list.php` | `GET` | **Yes** | Retrieves user's bookmarked jobs from MySQL. |
| `api/saved_jobs/toggle.php` | `POST` | **Yes** | Saves or removes a job bookmark. |
| `api/applications/submit.php` | `POST` | **Yes** | Submits job application, prevents duplicates, logs status audit trail. |
| `api/applications/list.php` | `GET` | **Yes** | Returns user's submitted job applications with real-time status. |
| `api/dashboard/stats.php` | `GET` | **Yes** | Aggregates live greeting, profile completion %, counts of saved jobs, applications, drives, notifications, and top recommended jobs. |
| `api/notifications/list.php` | `GET` | **Yes** | Returns user notifications. |
| `api/notifications/mark_read.php`| `POST` | **Yes** | Marks notifications as read. |

---

## 4. Android Frontend Architecture

- **HTTP Engine:** Retrofit 2.11.0 with OkHttp 4.12.0
- **JSON Converter:** Gson with custom type adapters and `@SerializedName` aliases
- **Auth Interceptor:** `RetrofitClient` injects `Authorization: Bearer <token>` automatically on every request when logged in.
- **Session Persistence:** `SessionManager` securely manages SharedPreferences with automatic login detection on splash launch.
- **Repository Pattern:** Repositories handle API requests with robust offline fallback data to ensure resilient UI operation.
- **State Management:** Jetpack Compose `StateFlow` + `viewModelScope` + `collectAsState()`.

