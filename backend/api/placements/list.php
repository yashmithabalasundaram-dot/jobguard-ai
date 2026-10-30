<?php
// =====================================================================
// JobGuard AI - Placement Drives List API
// Endpoint: GET /api/placements/list.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';

$db = getDatabaseConnection();
$stmt = $db->query("SELECT * FROM placement_drives ORDER BY created_at DESC");
$rows = $stmt->fetchAll();

$placements = [];
foreach ($rows as $row) {
    $rounds = json_decode($row['rounds'] ?? '[]', true) ?: [];
    $placements[] = [
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
    ];
}

jsonSuccess($placements, 'Placement drives retrieved');

