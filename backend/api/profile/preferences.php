<?php
// =====================================================================
// JobGuard AI - Career Preferences API
// Endpoint: GET /api/profile/preferences.php
//           POST /api/profile/preferences.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/validation.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$currentUser = requireAuth();
$db = getDatabaseConnection();

if ($_SERVER['REQUEST_METHOD'] === 'GET') {
    $stmt = $db->prepare("
        SELECT preferred_roles, preferred_locations, preferred_types, expected_salary, 
               consent_shared_with_companies, notifications_enabled 
        FROM user_preferences 
        WHERE user_id = :id
    ");
    $stmt->execute([':id' => $currentUser['id']]);
    $prefs = $stmt->fetch() ?: [
        'preferred_roles' => 'Android Developer',
        'preferred_locations' => 'Remote',
        'preferred_types' => 'Full-Time, Remote',
        'expected_salary' => '',
        'consent_shared_with_companies' => 1,
        'notifications_enabled' => 1
    ];
    jsonSuccess($prefs, 'Preferences retrieved');
}

if ($_SERVER['REQUEST_METHOD'] === 'POST') {
    $input = getJsonInput();

    $roles        = isset($input['preferred_roles']) ? sanitizeString(is_array($input['preferred_roles']) ? implode(', ', $input['preferred_roles']) : $input['preferred_roles']) : '';
    $locations    = isset($input['preferred_locations']) ? sanitizeString(is_array($input['preferred_locations']) ? implode(', ', $input['preferred_locations']) : $input['preferred_locations']) : '';
    $types        = isset($input['preferred_types']) ? sanitizeString($input['preferred_types']) : 'Full-Time, Remote';
    $salary       = isset($input['expected_salary']) ? sanitizeString($input['expected_salary']) : '';
    $consent      = isset($input['consent_shared_with_companies']) ? ($input['consent_shared_with_companies'] ? 1 : 0) : 1;
    $notifEnabled = isset($input['notifications_enabled']) ? ($input['notifications_enabled'] ? 1 : 0) : 1;

    $stmt = $db->prepare("
        INSERT INTO user_preferences (user_id, preferred_roles, preferred_locations, preferred_types, expected_salary, consent_shared_with_companies, notifications_enabled)
        VALUES (:id, :roles, :locations, :types, :salary, :consent, :notif)
        ON DUPLICATE KEY UPDATE
            preferred_roles = VALUES(preferred_roles),
            preferred_locations = VALUES(preferred_locations),
            preferred_types = VALUES(preferred_types),
            expected_salary = VALUES(expected_salary),
            consent_shared_with_companies = VALUES(consent_shared_with_companies),
            notifications_enabled = VALUES(notifications_enabled)
    ");
    $stmt->execute([
        ':id'        => $currentUser['id'],
        ':roles'     => $roles,
        ':locations' => $locations,
        ':types'     => $types,
        ':salary'    => $salary,
        ':consent'   => $consent,
        ':notif'     => $notifEnabled
    ]);

    jsonSuccess([
        'preferred_roles' => $roles,
        'preferred_locations' => $locations,
        'preferred_types' => $types,
        'expected_salary' => $salary,
        'consent_shared_with_companies' => (bool)$consent,
        'notifications_enabled' => (bool)$notifEnabled
    ], 'Preferences saved successfully');
}

jsonError('Method Not Allowed', 405);

