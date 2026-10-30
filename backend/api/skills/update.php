<?php
// =====================================================================
// JobGuard AI - Update User Skills API
// Endpoint: POST /api/skills/update.php
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

$skills = $input['skills'] ?? [];
if (!is_array($skills)) {
    // Comma-separated fallback
    $skills = array_filter(array_map('trim', explode(',', (string)$skills)));
}

$db = getDatabaseConnection();

try {
    $db->beginTransaction();

    // 1. Delete existing skills for user
    $delStmt = $db->prepare("DELETE FROM user_skills WHERE user_id = :id");
    $delStmt->execute([':id' => $currentUser['id']]);

    // 2. Insert new skills
    $insStmt = $db->prepare("
        INSERT INTO user_skills (user_id, skill_name, proficiency_level) 
        VALUES (:id, :skill, 'Intermediate')
    ");

    $savedSkills = [];
    foreach ($skills as $skill) {
        $skillName = sanitizeString($skill);
        if (!empty($skillName)) {
            $insStmt->execute([
                ':id'    => $currentUser['id'],
                ':skill' => $skillName
            ]);
            $savedSkills[] = $skillName;
        }
    }

    $db->commit();

    jsonSuccess([
        'user_id' => $currentUser['id'],
        'skills'  => $savedSkills,
        'count'   => count($savedSkills)
    ], 'Skills updated successfully');

} catch (Exception $e) {
    if ($db->inTransaction()) {
        $db->rollBack();
    }
    jsonError('Failed to update skills: ' . $e->getMessage(), 500);
}

