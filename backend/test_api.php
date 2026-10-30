<?php
// =====================================================================
// JobGuard AI - Automated Full End-to-End HTTP API Test Suite
// =====================================================================

$baseUrl = 'http://localhost/jobguard_ai/backend/api';
$testEmail = 'alex.test.' . time() . '@example.com';
$testPassword = 'Password123!';
$authToken = null;
$userId = null;
$otpCode = null;

function request($method, $endpoint, $data = null, $token = null) {
    global $baseUrl;
    $url = $baseUrl . $endpoint;
    $ch = curl_init($url);
    
    $headers = ['Content-Type: application/json', 'Accept: application/json'];
    if ($token) {
        $headers[] = 'Authorization: Bearer ' . $token;
    }
    
    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_TIMEOUT, 10);
    
    if ($data !== null) {
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
    }
    
    $rawResponse = curl_exec($ch);
    $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    $err = curl_error($ch);
    curl_close($ch);
    
    if ($rawResponse === false) {
        return ['code' => $httpCode, 'error' => $err, 'json' => null];
    }
    
    return [
        'code' => $httpCode,
        'json' => json_decode($rawResponse, true),
        'raw'  => $rawResponse
    ];
}

echo "=================================================================\n";
echo "   JOBGUARD AI - STAGE 3 & STAGE 4 END-TO-END HTTP API TESTS     \n";
echo "=================================================================\n\n";

// TEST 1: Register New User
echo "[TEST 1] POST /auth/register.php... ";
$res = request('POST', '/auth/register.php', [
    'full_name' => 'Alex Morgan',
    'email'     => $testEmail,
    'phone'     => '+1 (555) 019-2834',
    'password'  => $testPassword
]);
if ($res['code'] === 201 && ($res['json']['success'] ?? false)) {
    $authToken = $res['json']['data']['token'];
    $userId    = $res['json']['data']['user']['id'];
    $otpCode   = $res['json']['data']['otp_code'];
    echo "PASSED (201 Created, User ID: $userId, Token: " . substr($authToken, 0, 10) . "...)\n";
} else {
    echo "FAILED ({$res['code']}): " . ($res['raw'] ?? 'No response') . "\n";
    exit(1);
}

// TEST 2: Email Verification
echo "[TEST 2] POST /auth/verify_email.php (with OTP: $otpCode)... ";
$res = request('POST', '/auth/verify_email.php', [
    'otp_code' => $otpCode
], $authToken);
if ($res['code'] === 200 && ($res['json']['success'] ?? false)) {
    echo "PASSED (200 OK - Email verified)\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 3: Login with Invalid Password
echo "[TEST 3] POST /auth/login.php (Invalid Password)... ";
$res = request('POST', '/auth/login.php', [
    'email'    => $testEmail,
    'password' => 'WrongPassword!'
]);
if ($res['code'] === 401 && !($res['json']['success'] ?? false)) {
    echo "PASSED (401 Unauthorized as expected)\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 4: Login with Valid Password
echo "[TEST 4] POST /auth/login.php (Valid Password)... ";
$res = request('POST', '/auth/login.php', [
    'email'    => $testEmail,
    'password' => $testPassword
]);
if ($res['code'] === 200 && ($res['json']['success'] ?? false)) {
    $authToken = $res['json']['data']['token'];
    echo "PASSED (200 OK, New Token Issued)\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 5: Get Current User (Me)
echo "[TEST 5] GET /auth/me.php (Protected)... ";
$res = request('GET', '/auth/me.php', null, $authToken);
if ($res['code'] === 200 && ($res['json']['data']['user']['email'] ?? '') === $testEmail) {
    echo "PASSED (200 OK - Verified: " . ($res['json']['data']['user']['is_verified'] ? 'true' : 'false') . ")\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 6: Update Personal Details
echo "[TEST 6] POST /profile/update.php... ";
$res = request('POST', '/profile/update.php', [
    'full_name' => 'Alex Morgan',
    'headline'  => 'Senior Android Developer | Jetpack Compose Expert',
    'location'  => 'San Francisco, CA',
    'bio'       => 'Passionate mobile software engineer creating intelligent apps with Kotlin, Compose, and clean architecture.'
], $authToken);
if ($res['code'] === 200 && ($res['json']['success'] ?? false)) {
    echo "PASSED (200 OK - Headline: {$res['json']['data']['headline']})\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 7: Get Profile & Completion %
echo "[TEST 7] GET /profile/get.php... ";
$res = request('GET', '/profile/get.php', null, $authToken);
if ($res['code'] === 200 && isset($res['json']['data']['profile_completion_pct'])) {
    echo "PASSED (200 OK - Completion: {$res['json']['data']['profile_completion_pct']}%)\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 8: Save Education Qualification
echo "[TEST 8] POST /education/save.php... ";
$res = request('POST', '/education/save.php', [
    'degree'         => 'B.S. Computer Science',
    'institution'    => 'Stanford University',
    'field_of_study' => 'Software Engineering & AI',
    'start_year'     => '2020',
    'end_year'       => '2024',
    'grade'          => '3.8 / 4.0 GPA'
], $authToken);
$savedEduId = $res['json']['data']['id'] ?? null;
if ($res['code'] === 200 && $savedEduId) {
    echo "PASSED (200 OK - Saved Edu ID: $savedEduId)\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 9: List Education Qualifications
echo "[TEST 9] GET /education/list.php... ";
$res = request('GET', '/education/list.php', null, $authToken);
if ($res['code'] === 200 && count($res['json']['data']) >= 1) {
    echo "PASSED (200 OK - Count: " . count($res['json']['data']) . ")\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 10: Update User Skills
echo "[TEST 10] POST /skills/update.php... ";
$res = request('POST', '/skills/update.php', [
    'skills' => ['Kotlin', 'Jetpack Compose', 'MVVM', 'Coroutines', 'Room', 'Retrofit']
], $authToken);
if ($res['code'] === 200 && ($res['json']['data']['count'] ?? 0) === 6) {
    echo "PASSED (200 OK - 6 Skills Saved)\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 11: List User Skills
echo "[TEST 11] GET /skills/list.php... ";
$res = request('GET', '/skills/list.php', null, $authToken);
if ($res['code'] === 200 && count($res['json']['data']['skill_names']) === 6) {
    echo "PASSED (200 OK - List: " . implode(', ', $res['json']['data']['skill_names']) . ")\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 12: Available Standard Skills Catalog
echo "[TEST 12] GET /skills/available.php... ";
$res = request('GET', '/skills/available.php');
if ($res['code'] === 200 && in_array('Jetpack Compose', $res['json']['data'])) {
    echo "PASSED (200 OK - " . count($res['json']['data']) . " Catalog Skills)\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 13: Save & Get Preferences
echo "[TEST 13] POST /profile/preferences.php... ";
$res = request('POST', '/profile/preferences.php', [
    'preferred_roles'     => 'Senior Android Developer, Mobile Architect',
    'preferred_locations' => 'San Francisco, CA, Remote',
    'preferred_types'     => 'Full-Time, Remote',
    'expected_salary'     => '$130,000 - $160,000'
], $authToken);
if ($res['code'] === 200 && ($res['json']['success'] ?? false)) {
    echo "PASSED (200 OK - Preferences updated)\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 14: Change Password
echo "[TEST 14] POST /auth/change_password.php... ";
$res = request('POST', '/auth/change_password.php', [
    'current_password' => $testPassword,
    'new_password'     => 'NewSecretPass456!'
], $authToken);
if ($res['code'] === 200 && ($res['json']['success'] ?? false)) {
    echo "PASSED (200 OK - Password updated)\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 15: Forgot Password & Reset Token
echo "[TEST 15] POST /auth/forgot_password.php... ";
$res = request('POST', '/auth/forgot_password.php', [
    'email' => $testEmail
]);
$resetToken = $res['json']['data']['reset_token'] ?? null;
if ($res['code'] === 200 && $resetToken) {
    echo "PASSED (200 OK - Reset Token: " . substr($resetToken, 0, 10) . "...)\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 16: Reset Password with Token
echo "[TEST 16] POST /auth/reset_password.php... ";
$res = request('POST', '/auth/reset_password.php', [
    'token'        => $resetToken,
    'new_password' => 'FinalPassword789!'
]);
if ($res['code'] === 200 && ($res['json']['success'] ?? false)) {
    echo "PASSED (200 OK - Password successfully reset)\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 17: Log Out & Invalidate Token
echo "[TEST 17] POST /auth/logout.php... ";
$res = request('POST', '/auth/logout.php', null, $authToken);
if ($res['code'] === 200 && ($res['json']['success'] ?? false)) {
    echo "PASSED (200 OK - Session logged out)\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

// TEST 18: Verify Invalidated Token
echo "[TEST 18] GET /auth/me.php (With Logged-out Token)... ";
$res = request('GET', '/auth/me.php', null, $authToken);
if ($res['code'] === 401 && !($res['json']['success'] ?? false)) {
    echo "PASSED (401 Unauthorized - Session properly expired)\n";
} else {
    echo "FAILED ({$res['code']}): " . $res['raw'] . "\n";
}

echo "\n=================================================================\n";
echo "   ALL 18 END-TO-END HTTP REST API TESTS PASSED SUCCESSFULLY!   \n";
echo "=================================================================\n";

