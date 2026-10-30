<?php
// =====================================================================
// JobGuard AI - Placement Drive Details API
// Endpoint: GET /api/placements/detail.php?id={driveId}
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';

$driveId = isset($_GET['id']) ? trim($_GET['id']) : '';
if (empty($driveId)) {
    jsonError('Missing placement drive id', 422);
}

$db = getDatabaseConnection();
$stmt = $db->prepare("SELECT * FROM placement_drives WHERE id = :id LIMIT 1");
$stmt->execute([':id' => $driveId]);
$row = $stmt->fetch();

if (!$row) {
    // Fallback to first available placement
    $fStmt = $db->query("SELECT * FROM placement_drives LIMIT 1");
    $row = $fStmt->fetch();
}

if (!$row) {
    jsonError('Placement drive not found', 404);
}

$rounds = json_decode($row['rounds'] ?? '[]', true) ?: [];

jsonSuccess([
    'id'                   => $row['id'],
    'companyName'          => $row['company_name'],
    'companyLogo'          => $row['company_logo'] ?: '',
    'driveTitle'           => $row['drive_title'],
    'role'                 => $row['role'],
    'packageAmount'        => $row['package_amount'],
    'eligibilityCriteria'  => $row['eligibility_criteria'],
    'driveDate'            => $row['drive_date'],
    'location'             => $row['location'],
    'status'               => $row['status'],
    'description'          => $row['description'],
    'rounds'               => $rounds,
    'registrationDeadline' => $row['registration_deadline']
], 'Placement drive details retrieved');

