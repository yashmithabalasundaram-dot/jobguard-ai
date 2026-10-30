<?php
// =====================================================================
// JobGuard AI - Logout API
// Endpoint: POST /api/auth/logout.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonError('Method Not Allowed. Use POST.', 405);
}

$currentUser = requireAuth();

$db = getDatabaseConnection();
$stmt = $db->prepare("UPDATE users SET auth_token = NULL, token_expires_at = NULL WHERE id = :id");
$stmt->execute([':id' => $currentUser['id']]);

jsonSuccess(null, 'Logged out successfully. Session invalidated.');

