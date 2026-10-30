<?php
// =====================================================================
// JobGuard AI - Unified Home Dashboard Statistics & Feed API
// Endpoint: GET /api/dashboard/stats.php
// Aggregates real database counts, greeting, profile score, and recommendations
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$currentUser = requireAuth();
$db = getDatabaseConnection();

$userId = $currentUser['id'];

// 1. Personalized Greeting
$hour = intval(date('H'));
$greetingPrefix = 'Good Morning';
if ($hour >= 12 && $hour < 17) {
    $greetingPrefix = 'Good Afternoon';
} elseif ($hour >= 17) {
    $greetingPrefix = 'Good Evening';
}
$greeting = "$greetingPrefix, {$currentUser['full_name']} 👋";

// 2. Profile Completion (Dynamic accurate calculation based on actual records)
$pStmt = $db->prepare("SELECT headline, location, bio FROM user_profiles WHERE user_id = :uid");
$pStmt->execute([':uid' => $userId]);
$profile = $pStmt->fetch();

$profilePct = 20; // Base account creation
if (!empty($profile['headline'])) $profilePct += 15;
if (!empty($profile['location'])) $profilePct += 10;
if (!empty($profile['bio'])) $profilePct += 10;

// Check actual education
$eduStmt = $db->prepare("SELECT COUNT(*) FROM education_details WHERE user_id = :uid");
$eduStmt->execute([':uid' => $userId]);
if ($eduStmt->fetchColumn() > 0) $profilePct += 15;

// Check actual skills
$skillStmt = $db->prepare("SELECT COUNT(*) FROM user_skills WHERE user_id = :uid");
$skillStmt->execute([':uid' => $userId]);
if ($skillStmt->fetchColumn() > 0) $profilePct += 15;

// Check actual resume
$resCountStmt = $db->prepare("SELECT COUNT(*) FROM resumes WHERE user_id = :uid");
$resCountStmt->execute([':uid' => $userId]);
if ($resCountStmt->fetchColumn() > 0) $profilePct += 15;

$profilePct = min(100, $profilePct);

// Update user_profiles with exact percentage
$upStmt = $db->prepare("UPDATE user_profiles SET profile_completion_pct = :pct WHERE user_id = :uid");
$upStmt->execute([':pct' => $profilePct, ':uid' => $userId]);

// 3. Exact Database Metric Counts
// Saved Jobs
$savedCountStmt = $db->prepare("SELECT COUNT(*) FROM saved_jobs WHERE user_id = :uid");
$savedCountStmt->execute([':uid' => $userId]);
$totalSavedJobs = intval($savedCountStmt->fetchColumn());

// Confirmed Applications
$appCountStmt = $db->prepare("SELECT COUNT(*) FROM job_applications WHERE user_id = :uid");
$appCountStmt->execute([':uid' => $userId]);
$totalApplications = intval($appCountStmt->fetchColumn());

// Active Placement Opportunities
$placeCountStmt = $db->query("SELECT COUNT(*) FROM placement_drives WHERE status != 'Closed'");
$totalPlacements = intval($placeCountStmt->fetchColumn());

// Unread Notifications Count
$notifCountStmt = $db->prepare("SELECT COUNT(*) FROM notifications WHERE user_id = :uid AND is_read = 0");
$notifCountStmt->execute([':uid' => $userId]);
$unreadNotifications = intval($notifCountStmt->fetchColumn());

// 4. Default Resume Score & Optimization State
$rStmt = $db->prepare("
    SELECT r.id, r.file_name, a.overall_score 
    FROM resumes r 
    LEFT JOIN resume_analysis a ON r.id = a.resume_id 
    WHERE r.user_id = :uid 
    ORDER BY r.is_default DESC, r.created_at DESC 
    LIMIT 1
");
$rStmt->execute([':uid' => $userId]);
$resumeRecord = $rStmt->fetch();
$resumeScore = $resumeRecord ? intval($resumeRecord['overall_score'] ?: 0) : 0;
$resumeName = $resumeRecord ? $resumeRecord['file_name'] : null;

// 5. Recommended Jobs Feed (top 4 matching active jobs)
$jStmt = $db->prepare("
    SELECT j.*, 
           (SELECT COUNT(*) FROM saved_jobs sj WHERE sj.user_id = :uid AND sj.job_id = j.id) AS is_saved
    FROM jobs j
    WHERE j.is_active = 1
    ORDER BY j.created_at DESC
    LIMIT 4
");
$jStmt->execute([':uid' => $userId]);
$jobRows = $jStmt->fetchAll();

$recommendedJobs = [];
foreach ($jobRows as $row) {
    $requirements = json_decode($row['requirements'] ?? '[]', true) ?: [];
    $skills = json_decode($row['skills'] ?? '[]', true) ?: [];

    $recommendedJobs[] = [
        'id'            => $row['id'],
        'title'         => $row['title'],
        'company'       => $row['company_name'],
        'companyLogo'   => $row['company_logo'] ?: '',
        'location'      => $row['location'],
        'type'          => $row['type'],
        'salary'        => $row['salary'],
        'experience'    => $row['experience'],
        'description'   => $row['description'],
        'requirements'  => $requirements,
        'skills'        => $skills,
        'isSaved'       => (bool)$row['is_saved'],
        'isScamFlagged' => (bool)$row['is_scam_flagged'],
        'scamReason'    => $row['scam_reason'],
        'matchScore'    => intval($row['match_score']),
        'deadline'      => $row['deadline'] ?: '',
        'externalUrl'   => $row['external_url'] ?: ''
    ];
}

jsonSuccess([
    'greeting'            => $greeting,
    'userName'            => $currentUser['full_name'],
    'profileCompletion'   => $profilePct,
    'resumeScore'         => $resumeScore,
    'hasResume'           => !empty($resumeName),
    'resumeFileName'      => $resumeName,
    'totalSavedJobs'      => $totalSavedJobs,
    'totalApplications'   => $totalApplications,
    'totalPlacements'     => $totalPlacements,
    'unreadNotifications' => $unreadNotifications,
    'recommendedJobs'     => $recommendedJobs
], 'Dashboard statistics retrieved');

