<?php
// =====================================================================
// JobGuard AI - Verify Email API
// Endpoint: POST /api/auth/verify_email.php
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
$missing = requireFields($input, ['otp_code']);
if (!empty($missing)) {
    jsonError('Missing required field: otp_code', 422);
}

$otpCode = trim($input['otp_code']);
$email = !empty($input['email']) ? strtolower(trim($input['email'])) : null;

$db = getDatabaseConnection();

// Check if authenticated via Bearer token OR email provided
$user = null;
$token = getBearerToken();
if ($token) {
    $stmt = $db->prepare("SELECT id, email, verification_otp, otp_expires_at FROM users WHERE auth_token = :token LIMIT 1");
    $stmt->execute([':token' => $token]);
    $user = $stmt->fetch();
} elseif ($email) {
    $stmt = $db->prepare("SELECT id, email, verification_otp, otp_expires_at FROM users WHERE email = :email LIMIT 1");
    $stmt->execute([':email' => $email]);
    $user = $stmt->fetch();
}

if (!$user) {
    jsonError('User not found or authorization missing', 404);
}

// Check OTP match
if ($user['verification_otp'] !== $otpCode) {
    jsonError('Invalid verification code. Please check and try again.', 400);
}

if (!empty($user['otp_expires_at']) && strtotime($user['otp_expires_at']) < time()) {
    jsonError('Verification code has expired. Please request a new code.', 400);
}

// Mark verified
$updateStmt = $db->prepare("
    UPDATE users 
    SET is_verified = 1, verification_otp = NULL, otp_expires_at = NULL 
    WHERE id = :id
");
$updateStmt->execute([':id' => $user['id']]);

jsonSuccess([
    'verified' => true,
    'user_id'  => $user['id']
], 'Email successfully verified! Welcome aboard.');

