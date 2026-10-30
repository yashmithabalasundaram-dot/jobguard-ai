<?php
// =====================================================================
// JobGuard AI - Get Profile API
// Endpoint: GET /api/profile/get.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$currentUser = requireAuth();
$db = getDatabaseConnection();

$stmt = $db->prepare("
    SELECT u.id, u.full_name, u.email, u.phone, u.is_verified,
           p.headline, p.bio, p.location, p.profile_image_url, p.profile_completion_pct
    FROM users u
    LEFT JOIN user_profiles p ON u.id = p.user_id
    WHERE u.id = :id
    LIMIT 1
");
$stmt->execute([':id' => $currentUser['id']]);
$profile = $stmt->fetch();

// Calculate fresh completion percentage
$completion = 20; // baseline for created account
if (!empty($profile['headline'])) $completion += 15;
if (!empty($profile['location'])) $completion += 15;
if (!empty($profile['bio'])) $completion += 10;

// Check education
$eduStmt = $db->prepare("SELECT COUNT(*) FROM education_details WHERE user_id = :id");
$eduStmt->execute([':id' => $currentUser['id']]);
if ($eduStmt->fetchColumn() > 0) $completion += 15;

// Check skills
$skillStmt = $db->prepare("SELECT COUNT(*) FROM user_skills WHERE user_id = :id");
$skillStmt->execute([':id' => $currentUser['id']]);
if ($skillStmt->fetchColumn() > 0) $completion += 15;

// Check resume
$resStmt = $db->prepare("SELECT COUNT(*) FROM resumes WHERE user_id = :id");
$resStmt->execute([':id' => $currentUser['id']]);
if ($resStmt->fetchColumn() > 0) $completion += 10;

$completion = min(100, $completion);

if ($profile['profile_completion_pct'] != $completion) {
    $upStmt = $db->prepare("UPDATE user_profiles SET profile_completion_pct = :pct WHERE user_id = :id");
    $upStmt->execute([':pct' => $completion, ':id' => $currentUser['id']]);
    $profile['profile_completion_pct'] = $completion;
}

jsonSuccess($profile, 'Profile retrieved');

