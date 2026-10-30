<?php
// =====================================================================
// JobGuard AI - Toggle Bookmark / Save Job API
// Endpoint: POST /api/saved_jobs/toggle.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/validation.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonError('Method Not Allowed. Use POST.', 405);
}

$currentUser = requireAuth();
$input = getJsonInput();
$missing = requireFields($input, ['job_id']);
if (!empty($missing)) {
    jsonError('Missing required field: job_id', 422);
}

$jobId = trim($input['job_id']);
$db = getDatabaseConnection();

// Check if job exists
$jStmt = $db->prepare("SELECT id, title, company_name FROM jobs WHERE id = :id LIMIT 1");
$jStmt->execute([':id' => $jobId]);
$job = $jStmt->fetch();
if (!$job) {
    jsonError('Job not found', 404);
}

// Check if already saved
$checkStmt = $db->prepare("SELECT id FROM saved_jobs WHERE user_id = :uid AND job_id = :jid");
$checkStmt->execute([':uid' => $currentUser['id'], ':jid' => $jobId]);
$existing = $checkStmt->fetch();

if ($existing) {
    // Un-save
    $del = $db->prepare("DELETE FROM saved_jobs WHERE id = :id");
    $del->execute([':id' => $existing['id']]);
    jsonSuccess([
        'job_id'  => $jobId,
        'is_saved' => false
    ], 'Job removed from saved bookmarks');
} else {
    // Save
    $ins = $db->prepare("INSERT INTO saved_jobs (user_id, job_id) VALUES (:uid, :jid)");
    $ins->execute([':uid' => $currentUser['id'], ':jid' => $jobId]);
    jsonSuccess([
        'job_id'  => $jobId,
        'is_saved' => true
    ], 'Job saved successfully');
}

