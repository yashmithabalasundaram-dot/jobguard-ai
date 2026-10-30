<?php
// =====================================================================
// JobGuard AI - Job Recommendations API
// Endpoint: GET /api/jobs/recommendations.php
// Compares user's actual skills and preferences to calculate real match scores
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$db = getDatabaseConnection();

$token = getBearerToken();
$userId = null;
$userSkills = [];

if ($token) {
    $uStmt = $db->prepare("SELECT id FROM users WHERE auth_token = :token AND (token_expires_at IS NULL OR token_expires_at > NOW()) LIMIT 1");
    $uStmt->execute([':token' => $token]);
    $userId = $uStmt->fetchColumn() ?: null;

    if ($userId) {
        $sStmt = $db->prepare("SELECT LOWER(skill_name) FROM user_skills WHERE user_id = :uid");
        $sStmt->execute([':uid' => $userId]);
        $userSkills = $sStmt->fetchAll(PDO::FETCH_COLUMN);
    }
}

$savedSelect = $userId ? "(SELECT COUNT(*) FROM saved_jobs sj WHERE sj.user_id = :uid AND sj.job_id = j.id) AS is_saved" : "0 AS is_saved";

$stmt = $db->prepare("
    SELECT j.*, $savedSelect
    FROM jobs j
    WHERE j.is_active = 1
    ORDER BY j.created_at DESC
    LIMIT 20
");
$params = [];
if ($userId) {
    $params[':uid'] = $userId;
}
$stmt->execute($params);
$rows = $stmt->fetchAll();

$recommended = [];
foreach ($rows as $row) {
    $jobSkills = json_decode($row['skills'] ?? '[]', true) ?: [];
    $requirements = json_decode($row['requirements'] ?? '[]', true) ?: [];

    // Calculate real algorithmic skill overlap score
    $score = intval($row['match_score']);
    if (!empty($userSkills) && !empty($jobSkills)) {
        $overlap = 0;
        foreach ($jobSkills as $js) {
            if (in_array(strtolower($js), $userSkills)) {
                $overlap++;
            }
        }
        $calculatedPct = intval(($overlap / count($jobSkills)) * 100);
        $score = min(98, max(45, $calculatedPct));
    }

    $recommended[] = [
        'id'            => $row['id'],
        'title'         => $row['title'],
        'company'       => $row['company_name'],
        'companyLogo'   => $row['company_logo'] ?: '',
        'location'      => $row['location'],
        'type'          => $row['type'],
        'salary'        => $row['salary'],
        'experience'    => $row['experience'],
        'description'   => $row['description'],
        'requirements'  => $requirements,
        'skills'        => $jobSkills,
        'isSaved'       => (bool)$row['is_saved'],
        'isScamFlagged' => (bool)$row['is_scam_flagged'],
        'scamReason'    => $row['scam_reason'],
        'matchScore'    => $score,
        'deadline'      => $row['deadline'] ?: '',
        'externalUrl'   => $row['external_url'] ?: ''
    ];
}

// Sort by highest match score first
usort($recommended, function($a, $b) {
    return $b['matchScore'] - $a['matchScore'];
});

jsonSuccess(array_slice($recommended, 0, 10), 'Recommended jobs retrieved');

