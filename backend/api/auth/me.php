<?php
// =====================================================================
// JobGuard AI - Current Authenticated User API
// Endpoint: GET /api/auth/me.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$currentUser = requireAuth();

$db = getDatabaseConnection();

// Fetch Profile
$profileStmt = $db->prepare("
    SELECT headline, bio, location, profile_image_url, profile_completion_pct 
    FROM user_profiles 
    WHERE user_id = :id
");
$profileStmt->execute([':id' => $currentUser['id']]);
$profile = $profileStmt->fetch() ?: [
    'headline' => '',
    'bio' => '',
    'location' => '',
    'profile_image_url' => '',
    'profile_completion_pct' => 20
];

// Fetch Skills
$skillsStmt = $db->prepare("SELECT skill_name, proficiency_level FROM user_skills WHERE user_id = :id ORDER BY skill_name ASC");
$skillsStmt->execute([':id' => $currentUser['id']]);
$skills = $skillsStmt->fetchAll();

// Fetch Preferences
$prefStmt = $db->prepare("
    SELECT preferred_roles, preferred_locations, preferred_types, expected_salary, 
           consent_shared_with_companies, notifications_enabled 
    FROM user_preferences 
    WHERE user_id = :id
");
$prefStmt->execute([':id' => $currentUser['id']]);
$preferences = $prefStmt->fetch() ?: [
    'preferred_roles' => 'Android Developer',
    'preferred_locations' => 'Remote',
    'preferred_types' => 'Full-Time',
    'consent_shared_with_companies' => 1,
    'notifications_enabled' => 1
];

// Fetch Default Resume
$resStmt = $db->prepare("
    SELECT id, file_name, file_size, upload_date 
    FROM resumes 
    WHERE user_id = :id 
    ORDER BY is_default DESC, created_at DESC 
    LIMIT 1
");
$resStmt->execute([':id' => $currentUser['id']]);
$defaultResume = $resStmt->fetch();

jsonSuccess([
    'user' => [
        'id'          => $currentUser['id'],
        'full_name'   => $currentUser['full_name'],
        'email'       => $currentUser['email'],
        'phone'       => $currentUser['phone'],
        'is_verified' => (bool)$currentUser['is_verified'],
    ],
    'profile'        => $profile,
    'skills'         => array_column($skills, 'skill_name'),
    'preferences'    => $preferences,
    'default_resume' => $defaultResume
], 'Profile retrieved successfully');

