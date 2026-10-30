<?php
// =====================================================================
// JobGuard AI - Real User Persistence & Data Isolation Test Suite
// Verifies Tests 1 through 9:
// - Register User 1 & User 2
// - Login & verify correct user retrieval
// - Update profile & verify MySQL persistence
// - Add education & skills
// - Upload resume & verify metadata
// - Save jobs & submit applications
// - Verify user data isolation (User A != User B)
// - Verify dynamic dashboard counts
// =====================================================================

$baseUrl = 'http://localhost/jobguard_ai/backend/api';

function httpReq($method, $url, $payload = null, $token = null) {
    $ch = curl_init($url);
    $headers = [
        'Content-Type: application/json',
        'Accept: application/json'
    ];
    if ($token) {
        $headers[] = 'Authorization: Bearer ' . $token;
    }
    curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);
    curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
    curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);

    if ($payload !== null) {
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($payload));
    }

    $res = curl_exec($ch);
    $code = curl_getinfo($ch, CURLINFO_HTTP_CODE);
    curl_close($ch);

    return [
        'code' => $code,
        'body' => json_decode($res, true),
        'raw'  => $res
    ];
}

function assertTest($name, $condition, $details = '') {
    if ($condition) {
        echo "[PASSED] $name\n";
    } else {
        echo "[FAILED] $name: $details\n";
        exit(1);
    }
}

echo "=================================================================\n";
echo "   JOBGUARD AI - USER PERSISTENCE & DATA ISOLATION TESTS         \n";
echo "=================================================================\n\n";

$suffix = time();
$user1Email = "sarah.connor_{$suffix}@test.com";
$user1Pass  = "SarahSecure123!";
$user1Name  = "Sarah Connor";
$user1Phone = "+1 (555) 789-0123";

$user2Email = "john.reese_{$suffix}@test.com";
$user2Pass  = "JohnSecure456!";
$user2Name  = "John Reese";
$user2Phone = "+1 (555) 456-7890";

// TEST 1: Register User 1
$reg1 = httpReq('POST', "$baseUrl/auth/register.php", [
    'full_name' => $user1Name,
    'email'     => $user1Email,
    'phone'     => $user1Phone,
    'password'  => $user1Pass
]);
assertTest("TEST 1: Register Account 1 ($user1Name)", $reg1['code'] === 201 && !empty($reg1['body']['data']['token']));
$token1 = $reg1['body']['data']['token'];
$uid1   = $reg1['body']['data']['user']['id'];

// Verify in DB that it is NOT Alex Morgan
assertTest("TEST 1.1: Verified Account 1 name in payload", $reg1['body']['data']['user']['full_name'] === $user1Name);

// TEST 2: Login Account 1
$login1 = httpReq('POST', "$baseUrl/auth/login.php", [
    'email'    => $user1Email,
    'password' => $user1Pass
]);
assertTest("TEST 2: Login with Account 1", $login1['code'] === 200 && $login1['body']['data']['user']['id'] === $uid1);
$token1 = $login1['body']['data']['token'];

// TEST 3: Update Profile
$upd1 = httpReq('POST', "$baseUrl/profile/update.php", [
    'full_name' => $user1Name,
    'phone'     => $user1Phone,
    'headline'  => 'Lead Android Architect | Jetpack Compose Specialist',
    'location'  => 'Austin, TX',
    'bio'       => 'Building secure, high-performance mobile systems.'
], $token1);
assertTest("TEST 3: Update Profile for Account 1", $upd1['code'] === 200);

// Verify profile retrieval
$prof1 = httpReq('GET', "$baseUrl/profile/get.php", null, $token1);
assertTest("TEST 3.1: Verify Updated Headline persisted in MySQL", 
    $prof1['body']['data']['headline'] === 'Lead Android Architect | Jetpack Compose Specialist' &&
    $prof1['body']['data']['location'] === 'Austin, TX'
);

// TEST 4: Add Education & Skills
$edu1 = httpReq('POST', "$baseUrl/education/save.php", [
    'degree'         => 'M.S. Software Engineering',
    'institution'    => 'University of Texas at Austin',
    'field_of_study' => 'Computer Science',
    'start_year'     => '2021',
    'end_year'       => '2023',
    'grade'          => '3.95 GPA'
], $token1);
assertTest("TEST 4.1: Save Education for Account 1", $edu1['code'] === 200);

$skill1 = httpReq('POST', "$baseUrl/skills/update.php", [
    'skills' => ['Kotlin', 'Jetpack Compose', 'Coroutines', 'Room', 'CI/CD']
], $token1);
assertTest("TEST 4.2: Save 5 Skills for Account 1", $skill1['code'] === 200);

// TEST 5: Upload Resume
$res1 = httpReq('POST', "$baseUrl/resume/upload.php", [
    'file_name'   => "Sarah_Connor_Senior_Resume_{$suffix}.pdf",
    'parsed_text' => "Sarah Connor. Lead Android Architect. Proficient in Kotlin, Jetpack Compose, Room, Coroutines, CI/CD, and MVVM. M.S. Software Engineering."
], $token1);
assertTest("TEST 5: Upload Resume for Account 1", in_array($res1['code'], [200, 201]) && !empty($res1['body']['data']['id']));

// TEST 6: Save a Job & Submit Application
$jobsRes = httpReq('GET', "$baseUrl/jobs/search.php?limit=2");
$testJobId = $jobsRes['body']['data']['jobs'][0]['id'] ?? 'job_1';

$save1 = httpReq('POST', "$baseUrl/saved_jobs/toggle.php", [
    'job_id' => $testJobId
], $token1);
assertTest("TEST 6.1: Save Job for Account 1", $save1['code'] === 200 && $save1['body']['data']['is_saved'] === true);

$app1 = httpReq('POST', "$baseUrl/applications/submit.php", [
    'job_id'     => $testJobId,
    'resume_id'  => $res1['body']['data']['id'],
    'cover_note' => 'I would love to lead Android architecture for this team.'
], $token1);
assertTest("TEST 6.2: Submit Application for Account 1", $app1['code'] === 201);

// TEST 7: Dashboard Counts for Account 1
$dash1 = httpReq('GET', "$baseUrl/dashboard/stats.php", null, $token1);
assertTest("TEST 7: Verify Dynamic Dashboard Stats for Account 1", 
    $dash1['code'] === 200 &&
    $dash1['body']['data']['totalSavedJobs'] >= 1 &&
    $dash1['body']['data']['totalApplications'] >= 1 &&
    strpos($dash1['body']['data']['greeting'], $user1Name) !== false
);

// TEST 8: Register Account 2 (Verify Isolation)
$reg2 = httpReq('POST', "$baseUrl/auth/register.php", [
    'full_name' => $user2Name,
    'email'     => $user2Email,
    'phone'     => $user2Phone,
    'password'  => $user2Pass
]);
assertTest("TEST 8.1: Register Account 2 ($user2Name)", $reg2['code'] === 201);
$token2 = $reg2['body']['data']['token'];
$uid2   = $reg2['body']['data']['user']['id'];

// Check Account 2's initial dashboard (MUST be 0 saved jobs and 0 applications)
$dash2 = httpReq('GET', "$baseUrl/dashboard/stats.php", null, $token2);
assertTest("TEST 8.2: Account 2 Starts with 0 Saved Jobs & 0 Applications", 
    $dash2['body']['data']['totalSavedJobs'] === 0 &&
    $dash2['body']['data']['totalApplications'] === 0 &&
    strpos($dash2['body']['data']['greeting'], $user2Name) !== false
);

// Verify Account 2 does NOT see Account 1's saved jobs
$saved2 = httpReq('GET', "$baseUrl/saved_jobs/list.php", null, $token2);
assertTest("TEST 8.3: Data Isolation - Account 2 has 0 Saved Jobs", 
    $saved2['code'] === 200 && count($saved2['body']['data']) === 0
);

// Verify Account 2 does NOT see Account 1's applications
$apps2 = httpReq('GET', "$baseUrl/applications/list.php", null, $token2);
assertTest("TEST 8.4: Data Isolation - Account 2 has 0 Applications", 
    $apps2['code'] === 200 && count($apps2['body']['data']) === 0
);

// TEST 9: Verify Logout
$out1 = httpReq('POST', "$baseUrl/auth/logout.php", null, $token1);
assertTest("TEST 9.1: Logout Account 1", $out1['code'] === 200);

$meAfterLogout = httpReq('GET', "$baseUrl/auth/me.php", null, $token1);
assertTest("TEST 9.2: Token Revoked After Logout (401 Unauthorized)", $meAfterLogout['code'] === 401);

echo "\n=================================================================\n";
echo "   ALL 9 REAL PERSISTENCE & DATA ISOLATION TESTS PASSED!         \n";
echo "=================================================================\n";
