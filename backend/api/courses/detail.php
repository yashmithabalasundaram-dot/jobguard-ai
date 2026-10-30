<?php
// =====================================================================
// JobGuard AI - Course Details API
// Endpoint: GET /api/courses/detail.php?id={courseId}
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';

$courseId = isset($_GET['id']) ? trim($_GET['id']) : '';
if (empty($courseId)) {
    jsonError('Missing course id', 422);
}

$db = getDatabaseConnection();
$stmt = $db->prepare("SELECT * FROM courses WHERE id = :id LIMIT 1");
$stmt->execute([':id' => $courseId]);
$cr = $stmt->fetch();

if (!$cr) {
    $fStmt = $db->query("SELECT * FROM courses LIMIT 1");
    $cr = $fStmt->fetch();
}

if (!$cr) {
    jsonError('Course not found', 404);
}

$modules = json_decode($cr['modules'] ?? '[]', true) ?: [];

jsonSuccess([
    'id'             => $cr['id'],
    'title'          => $cr['title'],
    'provider'       => $cr['provider'],
    'duration'       => $cr['duration'],
    'level'          => $cr['level'],
    'skillCategory'  => $cr['skill_category'],
    'progressPercent'=> 25,
    'url'            => $cr['url'] ?: '',
    'description'    => $cr['description'],
    'modules'        => $modules,
    'isCompleted'    => false
], 'Course details retrieved');

