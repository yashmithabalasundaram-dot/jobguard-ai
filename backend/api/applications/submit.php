<?php
// =====================================================================
// JobGuard AI - Submit Job Application API
// Endpoint: POST /api/applications/submit.php
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
$coverNote = sanitizeString($input['cover_note'] ?? '');
$resumeId = !empty($input['resume_id']) ? trim($input['resume_id']) : null;

$db = getDatabaseConnection();

// 1. Verify Job exists
$jStmt = $db->prepare("SELECT id, title, company_name, company_logo FROM jobs WHERE id = :id LIMIT 1");
$jStmt->execute([':id' => $jobId]);
$job = $jStmt->fetch();
if (!$job) {
    jsonError('Target job does not exist or has closed', 404);
}

// 2. Prevent duplicate application
$dupStmt = $db->prepare("SELECT id, status, applied_date FROM job_applications WHERE user_id = :uid AND job_id = :jid LIMIT 1");
$dupStmt->execute([':uid' => $currentUser['id'], ':jid' => $jobId]);
$existingApp = $dupStmt->fetch();
if ($existingApp) {
    jsonError("You have already submitted an application for this vacancy on {$existingApp['applied_date']} (Status: {$existingApp['status']}).", 409);
}

// 3. Resolve default resume if not supplied
if (empty($resumeId)) {
    $rStmt = $db->prepare("SELECT id FROM resumes WHERE user_id = :uid ORDER BY is_default DESC, created_at DESC LIMIT 1");
    $rStmt->execute([':uid' => $currentUser['id']]);
    $resumeId = $rStmt->fetchColumn() ?: 'res_default';
}

$appId = 'app_' . bin2hex(random_bytes(8));
$appliedDate = date('d M Y');

try {
    $db->beginTransaction();

    // Insert Application
    $insApp = $db->prepare("
        INSERT INTO job_applications (id, user_id, job_id, resume_id, cover_note, status, applied_date)
        VALUES (:id, :uid, :jid, :rid, :note, 'Submitted', :date)
    ");
    $insApp->execute([
        ':id'   => $appId,
        ':uid'  => $currentUser['id'],
        ':jid'  => $jobId,
        ':rid'  => $resumeId,
        ':note' => $coverNote,
        ':date' => $appliedDate
    ]);

    // Insert Initial History Audit Log
    $insHist = $db->prepare("
        INSERT INTO application_status_history (application_id, status, notes)
        VALUES (:aid, 'Submitted', 'Application confirmed and delivered to employer review queue.')
    ");
    $insHist->execute([':aid' => $appId]);

    // Create Notification
    $notifStmt = $db->prepare("
        INSERT INTO notifications (id, user_id, title, message, type, is_read)
        VALUES (:id, :uid, 'Application Submitted 📝', :msg, 'ApplicationStatus', 0)
    ");
    $notifStmt->execute([
        ':id'  => 'notif_' . bin2hex(random_bytes(8)),
        ':uid' => $currentUser['id'],
        ':msg' => "Your application for {$job['title']} at {$job['company_name']} has been submitted."
    ]);

    $db->commit();

    jsonSuccess([
        'id'          => $appId,
        'jobId'       => $jobId,
        'jobTitle'    => $job['title'],
        'companyName' => $job['company_name'],
        'companyLogo' => $job['company_logo'] ?: '',
        'appliedDate' => $appliedDate,
        'status'      => 'Submitted',
        'resumeId'    => $resumeId,
        'coverNote'   => $coverNote,
        'isDemo'      => false
    ], 'Application submitted successfully', 201);

} catch (Exception $e) {
    if ($db->inTransaction()) {
        $db->rollBack();
    }
    jsonError('Failed to record application: ' . $e->getMessage(), 500);
}

