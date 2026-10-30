<?php
// =====================================================================
// JobGuard AI - Delete Education API
// Endpoint: POST /api/education/delete.php
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
$missing = requireFields($input, ['id']);
if (!empty($missing)) {
    jsonError('Missing required field: id', 422);
}

$eduId = trim($input['id']);
$db = getDatabaseConnection();

$stmt = $db->prepare("DELETE FROM education_details WHERE id = :id AND user_id = :user_id");
$stmt->execute([
    ':id'      => $eduId,
    ':user_id' => $currentUser['id']
]);

if ($stmt->rowCount() === 0) {
    jsonError('Education record not found or unauthorized to delete', 404);
}

jsonSuccess(null, 'Education record deleted successfully');

