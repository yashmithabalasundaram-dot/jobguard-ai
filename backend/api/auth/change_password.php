<?php
// =====================================================================
// JobGuard AI - Change Password API
// Endpoint: POST /api/auth/change_password.php
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
$missing = requireFields($input, ['current_password', 'new_password']);
if (!empty($missing)) {
    jsonError('Missing required fields: ' . implode(', ', $missing), 422);
}

$currentPass = trim($input['current_password']);
$newPass = trim($input['new_password']);

if (strlen($newPass) < 6) {
    jsonError('New password must be at least 6 characters long', 422);
}

$db = getDatabaseConnection();
$stmt = $db->prepare("SELECT password_hash FROM users WHERE id = :id LIMIT 1");
$stmt->execute([':id' => $currentUser['id']]);
$user = $stmt->fetch();

if (!$user || !password_verify($currentPass, $user['password_hash'])) {
    jsonError('The current password provided is incorrect', 400);
}

$newHash = password_hash($newPass, PASSWORD_BCRYPT);
$updateStmt = $db->prepare("UPDATE users SET password_hash = :hash WHERE id = :id");
$updateStmt->execute([
    ':hash' => $newHash,
    ':id'   => $currentUser['id']
]);

jsonSuccess(null, 'Password updated successfully');

