<?php
// =====================================================================
// JobGuard AI - Mark Notifications Read API
// Endpoint: POST /api/notifications/mark_read.php
//           POST /api/notifications/mark_all_read.php
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
$input = getJsonInput();

$notifId = isset($input['id']) ? trim($input['id']) : null;

if (!empty($notifId)) {
    // Mark specific notification as read
    $stmt = $db->prepare("UPDATE notifications SET is_read = 1 WHERE id = :id AND user_id = :uid");
    $stmt->execute([':id' => $notifId, ':uid' => $currentUser['id']]);
    jsonSuccess(['id' => $notifId, 'is_read' => true], 'Notification marked as read');
} else {
    // Mark all as read
    $stmt = $db->prepare("UPDATE notifications SET is_read = 1 WHERE user_id = :uid");
    $stmt->execute([':uid' => $currentUser['id']]);
    jsonSuccess(null, 'All notifications marked as read');
}

