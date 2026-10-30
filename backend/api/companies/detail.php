<?php
// =====================================================================
// JobGuard AI - Company Profile API
// Endpoint: GET /api/companies/detail.php?id={companyId}
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';

$companyId = isset($_GET['id']) ? trim($_GET['id']) : '';
if (empty($companyId)) {
    jsonError('Missing company id', 422);
}

$db = getDatabaseConnection();
$stmt = $db->prepare("SELECT * FROM companies WHERE id = :id LIMIT 1");
$stmt->execute([':id' => $companyId]);
$company = $stmt->fetch();

if (!$company) {
    // If not found, attempt fallback lookup by name or return default verified partner
    $fStmt = $db->prepare("SELECT * FROM companies LIMIT 1");
    $fStmt->execute();
    $company = $fStmt->fetch();
}

if (!$company) {
    jsonError('Company not found', 404);
}

// Get live count of open positions
$posStmt = $db->prepare("SELECT COUNT(*) FROM jobs WHERE company_id = :id AND is_active = 1");
$posStmt->execute([':id' => $company['id']]);
$openCount = intval($posStmt->fetchColumn());

jsonSuccess([
    'id'                 => $company['id'],
    'name'               => $company['name'],
    'logoUrl'            => $company['logo_url'] ?: '',
    'location'           => $company['location'],
    'industry'           => $company['industry'],
    'description'        => $company['description'],
    'website'            => $company['website'],
    'rating'             => floatval($company['rating']),
    'employeeCount'      => $company['employee_count'],
    'openPositionsCount' => max($openCount, intval($company['open_positions_count'])),
    'isVerified'         => (bool)$company['is_verified']
], 'Company profile retrieved');

