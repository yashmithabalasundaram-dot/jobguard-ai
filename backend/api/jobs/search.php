<?php
// =====================================================================
// JobGuard AI - Search Jobs API with Filtering & Pagination
// Endpoint: GET /api/jobs/search.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$db = getDatabaseConnection();

// Optional authentication to identify user's bookmarked jobs
$token = getBearerToken();
$userId = null;
if ($token) {
    $authStmt = $db->prepare("SELECT id FROM users WHERE auth_token = :token AND (token_expires_at IS NULL OR token_expires_at > NOW()) LIMIT 1");
    $authStmt->execute([':token' => $token]);
    $userId = $authStmt->fetchColumn() ?: null;
}

// Request parameters
$q          = isset($_GET['q']) ? trim($_GET['q']) : (isset($_GET['query']) ? trim($_GET['query']) : '');
$type       = isset($_GET['type']) ? trim($_GET['type']) : '';
$location   = isset($_GET['location']) ? trim($_GET['location']) : '';
$experience = isset($_GET['experience']) ? trim($_GET['experience']) : '';
$page       = max(1, isset($_GET['page']) ? intval($_GET['page']) : 1);
$limit      = max(1, min(50, isset($_GET['limit']) ? intval($_GET['limit']) : 15));
$offset     = ($page - 1) * $limit;

// Build dynamic WHERE clause
$where = ["j.is_active = 1"];
$params = [];

if ($q !== '') {
    $where[] = "(j.title LIKE :q OR j.company_name LIKE :q OR j.description LIKE :q OR j.skills LIKE :q)";
    $params[':q'] = '%' . $q . '%';
}

if ($type !== '' && strtolower($type) !== 'all') {
    $where[] = "j.type LIKE :type";
    $params[':type'] = '%' . $type . '%';
}

if ($location !== '' && strtolower($location) !== 'all') {
    $where[] = "j.location LIKE :loc";
    $params[':loc'] = '%' . $location . '%';
}

if ($experience !== '' && strtolower($experience) !== 'all') {
    $where[] = "j.experience LIKE :exp";
    $params[':exp'] = '%' . $experience . '%';
}

$whereClause = implode(' AND ', $where);

// Count total matching jobs
$countStmt = $db->prepare("SELECT COUNT(*) FROM jobs j WHERE $whereClause");
$countStmt->execute($params);
$totalJobs = intval($countStmt->fetchColumn());

// Query paginated results
$savedSelect = $userId ? "(SELECT COUNT(*) FROM saved_jobs sj WHERE sj.user_id = :uid AND sj.job_id = j.id) AS is_saved" : "0 AS is_saved";

$sql = "
    SELECT j.id, j.title, j.company_id, j.company_name, j.company_logo, j.location, j.type,
           j.salary, j.experience, j.description, j.requirements, j.skills, j.deadline,
           j.is_scam_flagged, j.scam_reason, j.match_score, j.external_url, j.created_at,
           $savedSelect
    FROM jobs j
    WHERE $whereClause
    ORDER BY j.created_at DESC
    LIMIT $limit OFFSET $offset
";

$stmt = $db->prepare($sql);
if ($userId) {
    $params[':uid'] = $userId;
}
$stmt->execute($params);
$rows = $stmt->fetchAll();

// Normalize JSON columns & booleans
$jobs = [];
foreach ($rows as $row) {
    $requirements = json_decode($row['requirements'] ?? '[]', true) ?: [];
    $skills = json_decode($row['skills'] ?? '[]', true) ?: [];

    $jobs[] = [
        'id'              => $row['id'],
        'title'           => $row['title'],
        'company'         => $row['company_name'],
        'companyLogo'     => $row['company_logo'] ?: '',
        'location'        => $row['location'],
        'type'            => $row['type'],
        'salary'          => $row['salary'],
        'experience'      => $row['experience'],
        'description'     => $row['description'],
        'requirements'    => $requirements,
        'skills'          => $skills,
        'isSaved'         => (bool)$row['is_saved'],
        'isScamFlagged'   => (bool)$row['is_scam_flagged'],
        'scamReason'      => $row['scam_reason'],
        'deadline'        => $row['deadline'] ?: '',
        'matchScore'      => intval($row['match_score']),
        'externalUrl'     => $row['external_url'] ?: '',
        'createdAt'       => $row['created_at']
    ];
}

$totalPages = ceil($totalJobs / $limit);

jsonSuccess([
    'total'       => $totalJobs,
    'page'        => $page,
    'pages'       => $totalPages,
    'limit'       => $limit,
    'count'       => count($jobs),
    'jobs'        => $jobs
], 'Jobs retrieved successfully');

