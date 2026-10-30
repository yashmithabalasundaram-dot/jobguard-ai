<?php
// =====================================================================
// JobGuard AI - Reset Password API
// Endpoint: POST /api/auth/reset_password.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/validation.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonError('Method Not Allowed. Use POST.', 405);
}

$input = getJsonInput();
$missing = requireFields($input, ['token', 'new_password']);
if (!empty($missing)) {
    jsonError('Missing required fields: ' . implode(', ', $missing), 422);
}

$token = trim($input['token']);
$newPassword = trim($input['new_password']);

if (strlen($newPassword) < 6) {
    jsonError('New password must be at least 6 characters long', 422);
}

$db = getDatabaseConnection();

$stmt = $db->prepare("
    SELECT email 
    FROM password_reset_tokens 
    WHERE token = :token AND expires_at > NOW() 
    LIMIT 1
");
$stmt->execute([':token' => $token]);
$record = $stmt->fetch();

if (!$record) {
    jsonError('Invalid or expired password reset token', 400);
}

$newHash = password_hash($newPassword, PASSWORD_BCRYPT);

try {
    $db->beginTransaction();

    // 1. Update User Password
    $updateStmt = $db->prepare("UPDATE users SET password_hash = :hash WHERE email = :email");
    $updateStmt->execute([
        ':hash'  => $newHash,
        ':email' => $record['email']
    ]);

    // 2. Invalidate reset token
    $delStmt = $db->prepare("DELETE FROM password_reset_tokens WHERE email = :email");
    $delStmt->execute([':email' => $record['email']]);

    $db->commit();

    jsonSuccess(null, 'Password has been successfully updated. You may now log in with your new password.');
} catch (Exception $e) {
    if ($db->inTransaction()) {
        $db->rollBack();
    }
    jsonError('Failed to reset password: ' . $e->getMessage(), 500);
}

