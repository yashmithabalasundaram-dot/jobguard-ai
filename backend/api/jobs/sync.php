<?php
// =====================================================================
// JobGuard AI - Synchronize Job Vacancies API
// Endpoint: POST /api/jobs/sync.php
// Triggers live sync from authentic sources (Arbeitnow, verified feeds)
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/services/JobAggregatorService.php';

$limit = isset($_GET['limit']) ? intval($_GET['limit']) : 50;
$result = JobAggregatorService::syncFromArbeitnow($limit);

if ($result['success']) {
    jsonSuccess($result, $result['message']);
} else {
    jsonError($result['message'], 502);
}

