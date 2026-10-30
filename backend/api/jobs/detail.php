<?php
// =====================================================================
// JobGuard AI - Job Details API
// Endpoint: GET /api/jobs/detail.php?id={jobId}
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$jobId = isset($_GET['id']) ? trim($_GET['id']) : '';
if (empty($jobId)) {
    jsonError('Missing required parameter: id', 422);
}

$db = getDatabaseConnection();

// Check if authenticated
$token = getBearerToken();
$userId = null;
if ($token) {
    $authStmt = $db->prepare("SELECT id FROM users WHERE auth_token = :token AND (token_expires_at IS NULL OR token_expires_at > NOW()) LIMIT 1");
    $authStmt->execute([':token' => $token]);
    $userId = $authStmt->fetchColumn() ?: null;
}

$savedSelect = $userId ? "(SELECT COUNT(*) FROM saved_jobs sj WHERE sj.user_id = :uid AND sj.job_id = j.id) AS is_saved" : "0 AS is_saved";

$stmt = $db->prepare("
    SELECT j.*, c.name AS company_title, c.website AS company_website, c.industry AS company_industry,
           c.rating AS company_rating, c.employee_count AS company_size,
           $savedSelect
    FROM jobs j
    LEFT JOIN companies c ON j.company_id = c.id
    WHERE j.id = :id
    LIMIT 1
");

$params = [':id' => $jobId];
if ($userId) {
    $params[':uid'] = $userId;
}
$stmt->execute($params);
$row = $stmt->fetch();

if (!$row) {
    jsonError('Job not found', 404);
}

$requirements = json_decode($row['requirements'] ?? '[]', true) ?: [];
$skills = json_decode($row['skills'] ?? '[]', true) ?: [];

// Generate safety report
$isScam = (bool)$row['is_scam_flagged'];
$warningFlags = [];
if ($isScam && !empty($row['scam_reason'])) {
    $warningFlags = array_map('trim', explode(';', $row['scam_reason']));
}

$safetyReport = [
    'isSuspicious'     => $isScam,
    'riskScorePercent' => $isScam ? 85 : 10,
    'warningFlags'     => $warningFlags,
    'recommendation'   => $isScam
        ? 'CAUTION: This listing exhibits suspicious indicators. Never pay upfront fees or provide financial account credentials.'
        : 'VERIFIED: This listing conforms to employer standards. Always verify recruiters via official company domains.'
];

$job = [
    'id'              => $row['id'],
    'title'           => $row['title'],
    'company'         => $row['company_name'],
    'companyLogo'     => $row['company_logo'] ?: '',
    'location'        => $row['location'],
    'type'            => $row['type'],
    'salary'          => $row['salary'],
    'experience'      => $row['experience'],
    'description'     => $row['description'],
    'requirements'    => $requirements,
    'skills'          => $skills,
    'isSaved'         => (bool)$row['is_saved'],
    'isScamFlagged'   => $isScam,
    'scamReason'      => $row['scam_reason'],
    'matchScore'      => intval($row['match_score']),
    'deadline'        => $row['deadline'] ?: '',
    'externalUrl'     => $row['external_url'] ?: '',
    'safetyReport'    => $safetyReport,
    'companyDetails'  => [
        'id'            => $row['company_id'],
        'name'          => $row['company_title'] ?: $row['company_name'],
        'industry'      => $row['company_industry'] ?: 'Technology',
        'website'       => $row['company_website'] ?: $row['external_url'],
        'rating'        => floatval($row['company_rating'] ?: 4.5),
        'employeeCount' => $row['company_size'] ?: '100-500'
    ]
];

jsonSuccess($job, 'Job details retrieved');

