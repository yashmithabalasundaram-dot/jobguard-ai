<?php
// =====================================================================
// JobGuard AI - Resend Verification Code API
// Endpoint: POST /api/auth/resend_otp.php
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

$input = getJsonInput();
$email = !empty($input['email']) ? strtolower(trim($input['email'])) : null;
$token = getBearerToken();

$db = getDatabaseConnection();
$user = null;

if ($token) {
    $stmt = $db->prepare("SELECT id, email FROM users WHERE auth_token = :token LIMIT 1");
    $stmt->execute([':token' => $token]);
    $user = $stmt->fetch();
} elseif ($email) {
    $stmt = $db->prepare("SELECT id, email FROM users WHERE email = :email LIMIT 1");
    $stmt->execute([':email' => $email]);
    $user = $stmt->fetch();
}

if (!$user) {
    jsonError('User account not found', 404);
}

$newOtp = str_pad((string)random_int(1000, 9999), 4, '0', STR_PAD_LEFT);

$updateStmt = $db->prepare("
    UPDATE users 
    SET verification_otp = :otp, otp_expires_at = DATE_ADD(NOW(), INTERVAL 24 HOUR) 
    WHERE id = :id
");
$updateStmt->execute([
    ':otp' => $newOtp,
    ':id'  => $user['id']
]);

jsonSuccess([
    'otp_code' => $newOtp,
    'email'    => $user['email']
], 'A new 4-digit verification code has been generated.');
