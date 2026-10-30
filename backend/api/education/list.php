<?php
// =====================================================================
// JobGuard AI - List Education Qualifications API
// Endpoint: GET /api/education/list.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$currentUser = requireAuth();
$db = getDatabaseConnection();

$stmt = $db->prepare("
    SELECT id, degree, institution, field_of_study, start_year, end_year, grade, created_at 
    FROM education_details 
    WHERE user_id = :id 
    ORDER BY end_year DESC, created_at DESC
");
$stmt->execute([':id' => $currentUser['id']]);
$educations = $stmt->fetchAll();

jsonSuccess($educations, 'Education list retrieved');

