<?php
// =====================================================================
// JobGuard AI - List User Skills API
// Endpoint: GET /api/skills/list.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$currentUser = requireAuth();
$db = getDatabaseConnection();

$stmt = $db->prepare("SELECT id, skill_name, proficiency_level FROM user_skills WHERE user_id = :id ORDER BY skill_name ASC");
$stmt->execute([':id' => $currentUser['id']]);
$skills = $stmt->fetchAll();

jsonSuccess([
    'skills'      => $skills,
    'skill_names' => array_column($skills, 'skill_name')
], 'Skills retrieved');

