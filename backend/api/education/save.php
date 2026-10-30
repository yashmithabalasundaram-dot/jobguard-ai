<?php
// =====================================================================
// JobGuard AI - Save (Create / Update) Education API
// Endpoint: POST /api/education/save.php
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

$missing = requireFields($input, ['degree', 'institution']);
if (!empty($missing)) {
    jsonError('Missing required fields: ' . implode(', ', $missing), 422);
}

$degree       = sanitizeString($input['degree']);
$institution  = sanitizeString($input['institution']);
$fieldOfStudy = sanitizeString($input['field_of_study'] ?? '');
$startYear    = sanitizeString($input['start_year'] ?? '');
$endYear      = sanitizeString($input['end_year'] ?? '');
$grade        = sanitizeString($input['grade'] ?? '');
$eduId        = !empty($input['id']) ? trim($input['id']) : ('edu_' . bin2hex(random_bytes(8)));

$db = getDatabaseConnection();

$stmt = $db->prepare("
    INSERT INTO education_details (id, user_id, degree, institution, field_of_study, start_year, end_year, grade)
    VALUES (:id, :user_id, :degree, :institution, :field, :start_yr, :end_yr, :grade)
    ON DUPLICATE KEY UPDATE
        degree = VALUES(degree),
        institution = VALUES(institution),
        field_of_study = VALUES(field_of_study),
        start_year = VALUES(start_year),
        end_year = VALUES(end_year),
        grade = VALUES(grade)
");
$stmt->execute([
    ':id'          => $eduId,
    ':user_id'     => $currentUser['id'],
    ':degree'      => $degree,
    ':institution' => $institution,
    ':field'       => $fieldOfStudy,
    ':start_yr'    => $startYear,
    ':end_yr'      => $endYear,
    ':grade'       => $grade
]);

jsonSuccess([
    'id'             => $eduId,
    'degree'         => $degree,
    'institution'    => $institution,
    'field_of_study' => $fieldOfStudy,
    'start_year'     => $startYear,
    'end_year'       => $endYear,
    'grade'          => $grade
], 'Education record saved successfully');

