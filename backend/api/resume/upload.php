<?php
// =====================================================================
// JobGuard AI - Resume Upload & AI Parsing API
// Endpoint: POST /api/resume/upload.php
// Accepts multipart/form-data with 'resume' file or base64 JSON
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/config/database.php';
require_once dirname(__DIR__, 2) . '/utils/response.php';
require_once dirname(__DIR__, 2) . '/middleware/validation.php';
require_once dirname(__DIR__, 2) . '/middleware/auth_middleware.php';
require_once dirname(__DIR__, 2) . '/services/ResumeParserService.php';

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    jsonError('Method Not Allowed. Use POST.', 405);
}

$currentUser = requireAuth();
$db = getDatabaseConnection();

$uploadDir = dirname(__DIR__, 2) . '/uploads/resumes/';
if (!is_dir($uploadDir)) {
    mkdir($uploadDir, 0755, true);
}

$fileName = '';
$fileSizeStr = '1.2 MB';
$fileType = 'application/pdf';
$savedFilePath = '';
$rawParsedText = '';

// Check if uploaded as standard multipart file
if (isset($_FILES['resume']) && $_FILES['resume']['error'] === UPLOAD_ERR_OK) {
    $fileInfo = $_FILES['resume'];
    $origName = basename($fileInfo['name']);
    $ext = strtolower(pathinfo($origName, PATHINFO_EXTENSION));

    $allowedExts = ['pdf', 'docx', 'doc', 'txt'];
    if (!in_array($ext, $allowedExts)) {
        jsonError('Invalid file type. Supported formats are PDF, DOCX, DOC, and TXT.', 422);
    }

    if ($fileInfo['size'] > 5 * 1024 * 1024) {
        jsonError('File size exceeds the 5MB maximum limit.', 422);
    }

    $fileSizeStr = round($fileInfo['size'] / (1024 * 1024), 2) . ' MB';
    $fileType = $fileInfo['type'] ?: 'application/pdf';
    $safeName = $currentUser['id'] . '_' . time() . '_' . preg_replace('/[^a-zA-Z0-9_\.-]/', '_', $origName);
    $savedFilePath = $uploadDir . $safeName;

    if (!move_uploaded_file($fileInfo['tmp_name'], $savedFilePath)) {
        jsonError('Failed to save uploaded file on server.', 500);
    }
    $fileName = $origName;
    $rawParsedText = ResumeParserService::extractTextFromFile($savedFilePath, $fileType);
} else {
    // Check JSON payload fallback (for testing or direct text/base64 uploads)
    $input = getJsonInput();
    $fileName = sanitizeString($input['file_name'] ?? 'Alex_Morgan_Resume_2025.pdf');
    $rawParsedText = sanitizeString($input['parsed_text'] ?? '');
    $savedFilePath = $uploadDir . $currentUser['id'] . '_sample_resume.pdf';
    if (!file_exists($savedFilePath)) {
        file_put_contents($savedFilePath, "%PDF-1.4 JobGuard AI Resume Content: " . $rawParsedText);
    }
}

// Perform AI evaluation
$analysis = ResumeParserService::analyze($rawParsedText, $fileName);
$resumeId = 'res_' . bin2hex(random_bytes(8));
$analysisId = 'ana_' . bin2hex(random_bytes(8));
$uploadDate = date('d M Y');

try {
    $db->beginTransaction();

    // Reset previous default resumes for user
    $db->prepare("UPDATE resumes SET is_default = 0 WHERE user_id = :uid")->execute([':uid' => $currentUser['id']]);

    // 1. Insert Resume
    $rStmt = $db->prepare("
        INSERT INTO resumes (id, user_id, file_name, file_path, file_size, file_type, parsed_text, is_default, upload_date)
        VALUES (:id, :uid, :name, :path, :size, :type, :text, 1, :date)
    ");
    $rStmt->execute([
        ':id'   => $resumeId,
        ':uid'  => $currentUser['id'],
        ':name' => $fileName,
        ':path' => $savedFilePath,
        ':size' => $fileSizeStr,
        ':type' => $fileType,
        ':text' => $analysis['parsed_text'],
        ':date' => $uploadDate
    ]);

    // 2. Insert Resume Analysis
    $aStmt = $db->prepare("
        INSERT INTO resume_analysis (id, resume_id, user_id, overall_score, extracted_skills, extracted_education, extracted_experience, missing_sections, improvement_suggestions)
        VALUES (:id, :res_id, :uid, :score, :skills, :edu, :exp, :missing, :sugg)
        ON DUPLICATE KEY UPDATE
            overall_score = VALUES(overall_score),
            extracted_skills = VALUES(extracted_skills),
            extracted_education = VALUES(extracted_education),
            extracted_experience = VALUES(extracted_experience),
            missing_sections = VALUES(missing_sections),
            improvement_suggestions = VALUES(improvement_suggestions)
    ");
    $aStmt->execute([
        ':id'      => $analysisId,
        ':res_id'  => $resumeId,
        ':uid'     => $currentUser['id'],
        ':score'   => $analysis['overall_score'],
        ':skills'  => json_encode($analysis['extracted_skills'], JSON_UNESCAPED_UNICODE),
        ':edu'     => json_encode($analysis['extracted_education'], JSON_UNESCAPED_UNICODE),
        ':exp'     => json_encode($analysis['extracted_experience'], JSON_UNESCAPED_UNICODE),
        ':missing' => json_encode($analysis['missing_sections'], JSON_UNESCAPED_UNICODE),
        ':sugg'    => json_encode($analysis['improvement_suggestions'], JSON_UNESCAPED_UNICODE)
    ]);

    // 3. Upsert extracted skills into user_skills
    $sStmt = $db->prepare("
        INSERT INTO user_skills (user_id, skill_name, proficiency_level)
        VALUES (:uid, :skill, 'Intermediate')
        ON DUPLICATE KEY UPDATE proficiency_level = 'Intermediate'
    ");
    foreach ($analysis['extracted_skills'] as $skill) {
        $sStmt->execute([':uid' => $currentUser['id'], ':skill' => $skill]);
    }

    // 4. Update profile completion %
    $db->prepare("UPDATE user_profiles SET profile_completion_pct = LEAST(100, profile_completion_pct + 15) WHERE user_id = :uid")
       ->execute([':uid' => $currentUser['id']]);

    // 5. Send Notification
    $db->prepare("
        INSERT INTO notifications (id, user_id, title, message, type, is_read)
        VALUES (:id, :uid, 'Resume Analysis Completed! 🚀', :msg, 'System', 0)
    ")->execute([
        ':id'  => 'notif_' . bin2hex(random_bytes(8)),
        ':uid' => $currentUser['id'],
        ':msg' => "Your resume scored {$analysis['overall_score']}/100. We found " . count($analysis['extracted_skills']) . " matched skills."
    ]);

    $db->commit();

    // Construct response matching Android Resume model
    $resumeModel = [
        'id'                     => $resumeId,
        'fileName'               => $fileName,
        'fileUri'                => 'http://localhost/jobguard_ai/backend/uploads/resumes/' . basename($savedFilePath),
        'uploadDate'             => $uploadDate,
        'fileSize'               => $fileSizeStr,
        'parsedText'             => $analysis['parsed_text'],
        'extractedSkills'        => $analysis['extracted_skills'],
        'extractedEducation'     => $analysis['extracted_education'],
        'extractedExperience'    => $analysis['extracted_experience'],
        'missingSections'        => $analysis['missing_sections'],
        'improvementSuggestions' => $analysis['improvement_suggestions'],
        'overallScore'           => $analysis['overall_score'],
        'isDefault'              => true
    ];

    jsonSuccess($resumeModel, 'Resume uploaded and analyzed successfully', 201);

} catch (Exception $e) {
    if ($db->inTransaction()) {
        $db->rollBack();
    }
    jsonError('Failed to process resume: ' . $e->getMessage(), 500);
}

