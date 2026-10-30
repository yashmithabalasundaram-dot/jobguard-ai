<?php
// =====================================================================
// JobGuard AI - User Login API
// Endpoint: POST /api/auth/login.php
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
$missing = requireFields($input, ['email', 'password']);
if (!empty($missing)) {
    jsonError('Missing required fields: ' . implode(', ', $missing), 422);
}

$email    = strtolower(trim($input['email']));
$password = trim($input['password']);

$db = getDatabaseConnection();

$stmt = $db->prepare("
    SELECT u.id, u.full_name, u.email, u.phone, u.password_hash, u.is_verified,
           p.headline, p.bio, p.location, p.profile_completion_pct
    FROM users u
    LEFT JOIN user_profiles p ON u.id = p.user_id
    WHERE u.email = :email
    LIMIT 1
");
$stmt->execute([':email' => $email]);
$user = $stmt->fetch();

if (!$user || !password_verify($password, $user['password_hash'])) {
    jsonError('Invalid email address or password', 401);
}

// Generate new fresh secure auth token
$newToken = bin2hex(random_bytes(32));

$updateStmt = $db->prepare("
    UPDATE users 
    SET auth_token = :token, token_expires_at = DATE_ADD(NOW(), INTERVAL 30 DAY) 
    WHERE id = :id
");
$updateStmt->execute([
    ':token' => $newToken,
    ':id'    => $user['id']
]);

// Strip password hash from response
unset($user['password_hash']);

jsonSuccess([
    'token' => $newToken,
    'user'  => $user
], 'Login successful');
