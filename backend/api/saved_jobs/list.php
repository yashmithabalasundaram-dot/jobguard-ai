<?php
// =====================================================================
// JobGuard AI - List Saved Jobs API
// Endpoint: GET /api/saved_jobs/list.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$currentUser = requireAuth();
$db = getDatabaseConnection();

$stmt = $db->prepare("
    SELECT j.*, sj.saved_at, 1 AS is_saved
    FROM saved_jobs sj
    JOIN jobs j ON sj.job_id = j.id
    WHERE sj.user_id = :uid
    ORDER BY sj.saved_at DESC
");
$stmt->execute([':uid' => $currentUser['id']]);
$rows = $stmt->fetchAll();

$savedJobs = [];
foreach ($rows as $row) {
    $requirements = json_decode($row['requirements'] ?? '[]', true) ?: [];
    $skills = json_decode($row['skills'] ?? '[]', true) ?: [];

    $savedJobs[] = [
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
        'isSaved'       => true,
        'isScamFlagged' => (bool)$row['is_scam_flagged'],
        'scamReason'    => $row['scam_reason'],
        'deadline'      => $row['deadline'] ?: '',
        'matchScore'    => intval($row['match_score']),
        'externalUrl'   => $row['external_url'] ?: ''
    ];
}

jsonSuccess($savedJobs, 'Saved jobs retrieved');

