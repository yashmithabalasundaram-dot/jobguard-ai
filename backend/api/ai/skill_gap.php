<?php
// =====================================================================
// JobGuard AI - Skill Gap Analysis API
// Endpoint: GET /api/ai/skill_gap.php?target_role={role}
// Compares user's actual skills with target role requirements
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$currentUser = requireAuth();
$db = getDatabaseConnection();

$targetRole = isset($_GET['target_role']) ? trim($_GET['target_role']) : 'Senior Android Developer';

// Fetch user's registered skills
$uSkillsStmt = $db->prepare("SELECT skill_name FROM user_skills WHERE user_id = :uid");
$uSkillsStmt->execute([':uid' => $currentUser['id']]);
$userSkills = $uSkillsStmt->fetchAll(PDO::FETCH_COLUMN);

if (empty($userSkills)) {
    $userSkills = [];
}

// Define target role benchmark skills
$standardBenchmark = [
    'Kotlin', 'Jetpack Compose', 'Coroutines', 'MVVM',
    'Room', 'Retrofit', 'StateFlow', 'Unit Testing', 'CI/CD'
];

$matchedSkills = [];
$missingSkills = [];
$lowerUser = array_map('strtolower', $userSkills);

foreach ($standardBenchmark as $skill) {
    if (in_array(strtolower($skill), $lowerUser)) {
        $matchedSkills[] = $skill;
    } else {
        $missingSkills[] = $skill;
    }
}

$totalBenchmark = count($standardBenchmark);
$readiness = intval((count($matchedSkills) / max(1, $totalBenchmark)) * 100);
$readiness = min(100, max(20, $readiness));

// Fetch recommended courses for missing skills
$courses = [];
if (!empty($missingSkills)) {
    $placeholders = implode(',', array_fill(0, count($missingSkills), '?'));
    $cStmt = $db->prepare("SELECT * FROM courses WHERE is_active = 1 AND skill_category IN ($placeholders) LIMIT 5");
    $cStmt->execute($missingSkills);
    $courseRows = $cStmt->fetchAll();

    foreach ($courseRows as $cr) {
        $modules = json_decode($cr['modules'] ?? '[]', true) ?: [];
        $courses[] = [
            'id'             => $cr['id'],
            'title'          => $cr['title'],
            'provider'       => $cr['provider'],
            'duration'       => $cr['duration'],
            'level'          => $cr['level'],
            'skillCategory'  => $cr['skill_category'],
            'progressPercent'=> 0,
            'url'            => $cr['url'] ?: '',
            'description'    => $cr['description'],
            'modules'        => $modules,
            'isCompleted'    => false
        ];
    }
}

// If no matching course in DB, fetch general courses
if (empty($courses)) {
    $genStmt = $db->query("SELECT * FROM courses WHERE is_active = 1 LIMIT 3");
    while ($cr = $genStmt->fetch()) {
        $modules = json_decode($cr['modules'] ?? '[]', true) ?: [];
        $courses[] = [
            'id'             => $cr['id'],
            'title'          => $cr['title'],
            'provider'       => $cr['provider'],
            'duration'       => $cr['duration'],
            'level'          => $cr['level'],
            'skillCategory'  => $cr['skill_category'],
            'progressPercent'=> 0,
            'url'            => $cr['url'] ?: '',
            'description'    => $cr['description'],
            'modules'        => $modules,
            'isCompleted'    => false
        ];
    }
}

jsonSuccess([
    'targetRole'          => $targetRole,
    'matchedSkills'       => $matchedSkills,
    'missingSkills'       => $missingSkills,
    'readinessPercentage' => $readiness,
    'recommendedCourses'  => $courses
], 'Skill gap analysis generated');

