<?php
// =====================================================================
// JobGuard AI - Forgot Password API
// Endpoint: POST /api/auth/forgot_password.php
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
$missing = requireFields($input, ['email']);
if (!empty($missing)) {
    jsonError('Missing required field: email', 422);
}

$email = strtolower(trim($input['email']));
if (!validateEmail($email)) {
    jsonError('Invalid email format', 422);
}

$db = getDatabaseConnection();
$stmt = $db->prepare("SELECT id, email FROM users WHERE email = :email LIMIT 1");
$stmt->execute([':email' => $email]);
$user = $stmt->fetch();

if (!$user) {
    // Return friendly generic success response to prevent email enumeration
    jsonSuccess([
        'message' => 'If an account exists with this email, a reset token has been issued.'
    ]);
}

$resetToken = bin2hex(random_bytes(24));

// Clean old tokens for this email
$cleanStmt = $db->prepare("DELETE FROM password_reset_tokens WHERE email = :email");
$cleanStmt->execute([':email' => $email]);

// Insert new token using MySQL NOW() to guarantee timezone consistency
$insertStmt = $db->prepare("
    INSERT INTO password_reset_tokens (email, token, expires_at)
    VALUES (:email, :token, DATE_ADD(NOW(), INTERVAL 1 HOUR))
");
$insertStmt->execute([
    ':email' => $email,
    ':token' => $resetToken
]);

jsonSuccess([
    'reset_token' => $resetToken,
    'expires_in'  => '1 hour',
    'message'     => 'Password reset token generated successfully.'
], 'Password reset instructions sent');
