<?php
// =====================================================================
// JobGuard AI - Update Personal Profile API
// Endpoint: POST /api/profile/update.php
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

$fullName = isset($input['full_name']) ? sanitizeString($input['full_name']) : $currentUser['full_name'];
$phone    = isset($input['phone']) ? sanitizeString($input['phone']) : $currentUser['phone'];
$headline = isset($input['headline']) ? sanitizeString($input['headline']) : '';
$bio      = isset($input['bio']) ? sanitizeString($input['bio']) : '';
$location = isset($input['location']) ? sanitizeString($input['location']) : '';

$db = getDatabaseConnection();

try {
    $db->beginTransaction();

    // 1. Update users table
    $uStmt = $db->prepare("UPDATE users SET full_name = :name, phone = :phone WHERE id = :id");
    $uStmt->execute([
        ':name'  => $fullName,
        ':phone' => $phone,
        ':id'    => $currentUser['id']
    ]);

    // 2. Upsert user_profiles table
    $pStmt = $db->prepare("
        INSERT INTO user_profiles (user_id, headline, bio, location)
        VALUES (:id, :headline, :bio, :location)
        ON DUPLICATE KEY UPDATE 
            headline = VALUES(headline),
            bio = VALUES(bio),
            location = VALUES(location)
    ");
    $pStmt->execute([
        ':id'       => $currentUser['id'],
        ':headline' => $headline,
        ':bio'      => $bio,
        ':location' => $location
    ]);

    $db->commit();

    jsonSuccess([
        'full_name' => $fullName,
        'phone'     => $phone,
        'headline'  => $headline,
        'bio'       => $bio,
        'location'  => $location
    ], 'Personal profile updated successfully');

} catch (Exception $e) {
    if ($db->inTransaction()) {
        $db->rollBack();
    }
    jsonError('Failed to update profile: ' . $e->getMessage(), 500);
}

