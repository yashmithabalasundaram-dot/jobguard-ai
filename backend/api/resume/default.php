<?php
// =====================================================================
// JobGuard AI - Get Default Resume & Analysis API
// Endpoint: GET /api/resume/default.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';

$currentUser = requireAuth();
$db = getDatabaseConnection();

$stmt = $db->prepare("
    SELECT r.*, a.overall_score, a.extracted_skills, a.extracted_education,
           a.extracted_experience, a.missing_sections, a.improvement_suggestions
    FROM resumes r
    LEFT JOIN resume_analysis a ON r.id = a.resume_id
    WHERE r.user_id = :uid
    ORDER BY r.is_default DESC, r.created_at DESC
    LIMIT 1
");
$stmt->execute([':uid' => $currentUser['id']]);
$row = $stmt->fetch();

if (!$row) {
    jsonSuccess(null, 'No resume uploaded yet');
}

$extractedSkills = json_decode($row['extracted_skills'] ?? '[]', true) ?: [];
$extractedEducation = json_decode($row['extracted_education'] ?? '[]', true) ?: [];
$extractedExperience = json_decode($row['extracted_experience'] ?? '[]', true) ?: [];
$missingSections = json_decode($row['missing_sections'] ?? '[]', true) ?: [];
$improvementSuggestions = json_decode($row['improvement_suggestions'] ?? '[]', true) ?: [];

$resume = [
    'id'                     => $row['id'],
    'fileName'               => $row['file_name'],
    'fileUri'                => 'http://localhost/jobguard_ai/backend/uploads/resumes/' . basename($row['file_path']),
    'uploadDate'             => $row['upload_date'],
    'fileSize'               => $row['file_size'],
    'parsedText'             => $row['parsed_text'] ?: '',
    'extractedSkills'        => $extractedSkills,
    'extractedEducation'     => $extractedEducation,
    'extractedExperience'    => $extractedExperience,
    'missingSections'        => $missingSections,
    'improvementSuggestions' => $improvementSuggestions,
    'overallScore'           => intval($row['overall_score'] ?: 85),
    'isDefault'              => (bool)$row['is_default']
];

jsonSuccess($resume, 'Resume retrieved');

