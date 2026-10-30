<?php
// =====================================================================
// JobGuard AI - List User's Job Applications API
// Endpoint: GET /api/applications/list.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$currentUser = requireAuth();
$db = getDatabaseConnection();

$stmt = $db->prepare("
    SELECT a.id, a.job_id, a.resume_id, a.cover_note, a.status, a.applied_date,
           j.title AS job_title, j.company_name, j.company_logo
    FROM job_applications a
    JOIN jobs j ON a.job_id = j.id
    WHERE a.user_id = :uid
    ORDER BY a.created_at DESC
");
$stmt->execute([':uid' => $currentUser['id']]);
$rows = $stmt->fetchAll();

$applications = [];
foreach ($rows as $row) {
    $applications[] = [
        'id'          => $row['id'],
        'jobId'       => $row['job_id'],
        'jobTitle'    => $row['job_title'],
        'companyName' => $row['company_name'],
        'companyLogo' => $row['company_logo'] ?: '',
        'appliedDate' => $row['applied_date'],
        'status'      => $row['status'],
        'resumeId'    => $row['resume_id'] ?: '',
        'coverNote'   => $row['cover_note'] ?: '',
        'isDemo'      => false
    ];
}

jsonSuccess($applications, 'Applications retrieved');

