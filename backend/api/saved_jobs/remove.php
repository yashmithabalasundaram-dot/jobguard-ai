<?php
// =====================================================================
// JobGuard AI - Remove Saved Job API
// Endpoint: POST /api/saved_jobs/remove.php
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

$del = $db->prepare("DELETE FROM saved_jobs WHERE user_id = :uid AND job_id = :jid");
$del->execute([':uid' => $currentUser['id'], ':jid' => $jobId]);

jsonSuccess([
    'job_id'  => $jobId,
    'is_saved' => false
], 'Saved job removed');

