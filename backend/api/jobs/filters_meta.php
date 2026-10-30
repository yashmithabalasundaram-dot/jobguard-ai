<?php
// =====================================================================
// JobGuard AI - Job Filters Metadata API
// Endpoint: GET /api/jobs/filters_meta.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';

$db = getDatabaseConnection();

$types = $db->query("SELECT DISTINCT type FROM jobs WHERE is_active = 1 AND type != '' LIMIT 10")->fetchAll(PDO::FETCH_COLUMN);
$locations = $db->query("SELECT DISTINCT location FROM jobs WHERE is_active = 1 AND location != '' LIMIT 10")->fetchAll(PDO::FETCH_COLUMN);

jsonSuccess([
    'types'      => !empty($types) ? $types : ['Full-Time', 'Remote', 'Contract', 'Part-Time', 'Internship'],
    'locations'  => $locations,
    'experience' => ['All', '0-2 yrs (Junior)', '2-4 yrs (Mid)', '4-7 yrs (Senior)', '7+ yrs (Lead)']
], 'Filters metadata retrieved');

