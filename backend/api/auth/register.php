<?php
// =====================================================================
// JobGuard AI - User Registration API
// Endpoint: POST /api/auth/register.php
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
$missing = requireFields($input, ['full_name', 'email', 'password']);
if (!empty($missing)) {
    jsonError('Missing required fields: ' . implode(', ', $missing), 422);
}

$fullName = sanitizeString($input['full_name']);
$email    = strtolower(trim($input['email']));
$phone    = sanitizeString($input['phone'] ?? '');
$password = trim($input['password']);

if (!validateEmail($email)) {
    jsonError('Invalid email address format', 422);
}

if (strlen($password) < 6) {
    jsonError('Password must be at least 6 characters long', 422);
}

$db = getDatabaseConnection();

// Check if email already registered
$checkStmt = $db->prepare("SELECT id FROM users WHERE email = :email LIMIT 1");
$checkStmt->execute([':email' => $email]);
if ($checkStmt->fetch()) {
    jsonError('An account with this email address already exists. Please log in.', 409);
}

$userId = 'usr_' . bin2hex(random_bytes(10));
$passwordHash = password_hash($password, PASSWORD_BCRYPT);
$authToken = bin2hex(random_bytes(32));
$otpCode = str_pad((string)random_int(1000, 9999), 4, '0', STR_PAD_LEFT);

try {
    $db->beginTransaction();

    // 1. Insert User (using MySQL DATE_ADD for accurate timezone handling)
    $userStmt = $db->prepare("
        INSERT INTO users (id, full_name, email, phone, password_hash, is_verified, verification_otp, otp_expires_at, auth_token, token_expires_at)
        VALUES (:id, :name, :email, :phone, :hash, 0, :otp, DATE_ADD(NOW(), INTERVAL 24 HOUR), :token, DATE_ADD(NOW(), INTERVAL 30 DAY))
    ");
    $userStmt->execute([
        ':id'    => $userId,
        ':name'  => $fullName,
        ':email' => $email,
        ':phone' => $phone,
        ':hash'  => $passwordHash,
        ':otp'   => $otpCode,
        ':token' => $authToken
    ]);

    // 2. Insert Initial User Profile
    $profileStmt = $db->prepare("
        INSERT INTO user_profiles (user_id, headline, bio, location, profile_completion_pct)
        VALUES (:user_id, '', '', '', 20)
    ");
    $profileStmt->execute([':user_id' => $userId]);

    // 3. Insert Initial Preferences
    $prefStmt = $db->prepare("
        INSERT INTO user_preferences (user_id, preferred_roles, preferred_locations, preferred_types, consent_shared_with_companies, notifications_enabled)
        VALUES (:user_id, '', '', '', 1, 1)
    ");
    $prefStmt->execute([':user_id' => $userId]);

    // 4. Create Initial Welcome Notification
    $notifStmt = $db->prepare("
        INSERT INTO notifications (id, user_id, title, message, type, is_read)
        VALUES (:id, :user_id, 'Welcome to JobGuard AI! 🛡️', 'Your secure account has been created. Complete your profile and upload your resume to get AI-matched jobs.', 'System', 0)
    ");
    $notifStmt->execute([
        ':id'      => 'notif_' . bin2hex(random_bytes(8)),
        ':user_id' => $userId
    ]);

    $db->commit();

    jsonSuccess([
        'token'       => $authToken,
        'user'        => [
            'id'          => $userId,
            'full_name'   => $fullName,
            'email'       => $email,
            'phone'       => $phone,
            'is_verified' => false
        ],
        'otp_code'    => $otpCode, // Provided for easy development / verification on Android
        'message'     => 'Registration successful. Verification code generated.'
    ], 'Account created successfully', 201);

} catch (Exception $e) {
    if ($db->inTransaction()) {
        $db->rollBack();
    }
    jsonError('Registration failed: ' . $e->getMessage(), 500);
}
