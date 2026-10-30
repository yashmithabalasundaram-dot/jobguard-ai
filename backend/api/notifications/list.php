<?php
// =====================================================================
// JobGuard AI - List Notifications API
// Endpoint: GET /api/notifications/list.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$currentUser = requireAuth();
$db = getDatabaseConnection();

$stmt = $db->prepare("
    SELECT id, title, message, created_at, is_read, type 
    FROM notifications 
    WHERE user_id = :uid 
    ORDER BY created_at DESC 
    LIMIT 30
");
$stmt->execute([':uid' => $currentUser['id']]);
$rows = $stmt->fetchAll();

$notifications = [];
foreach ($rows as $row) {
    // Relative humanized timestamp
    $time = strtotime($row['created_at']);
    $diff = time() - $time;
    if ($diff < 60) {
        $timeStr = "Just now";
    } elseif ($diff < 3600) {
        $timeStr = floor($diff / 60) . " mins ago";
    } elseif ($diff < 86400) {
        $timeStr = floor($diff / 3600) . " hours ago";
    } else {
        $timeStr = date('d M', $time);
    }

    $notifications[] = [
        'id'        => $row['id'],
        'title'     => $row['title'],
        'message'   => $row['message'],
        'timestamp' => $timeStr,
        'isRead'    => (bool)$row['is_read'],
        'type'      => $row['type']
    ];
}

jsonSuccess($notifications, 'Notifications retrieved');

