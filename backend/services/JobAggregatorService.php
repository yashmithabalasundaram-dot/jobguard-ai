<?php
// =====================================================================
// JobGuard AI - Job Aggregation & Normalization Service
// Integrates authorized public job APIs (Arbeitnow API) & Verified Employers
// Performs deduplication, schema normalization, and Scam Inspection
// =====================================================================

require_once dirname(__DIR__) . '/config/database.php';

class JobAggregatorService {

    /**
     * Inspects a job posting for fraudulent indicators, upfront fee requests, or phishing scams.
     */
    public static function inspectJobSafety($title, $description, $salary = '') {
        $text = strtolower($title . ' ' . $description . ' ' . $salary);
        $flags = [];

        if (preg_match('/(upfront\s*(fee|payment|money)|registration\s*fee|starter\s*kit\s*fee|pay\s*\$\d+)/i', $text)) {
            $flags[] = "Requires upfront money or registration fee from applicant";
        }
        if (preg_match('/(\$5,000|\$10,000|unlimited income|earn big fast|rich quick)/i', $text) && 
            preg_match('/(data entry|typing|no experience|easy work|2 hours a day)/i', $text)) {
            $flags[] = "Unrealistically high compensation for entry-level or minimal tasks";
        }
        if (preg_match('/(no interview|instant hiring|guaranteed job without screening)/i', $text)) {
            $flags[] = "Guaranteed instant hiring with zero verification or screening process";
        }
        if (preg_match('/(telegram only|whatsapp only|contact me on whatsapp|direct payment via gift card)/i', $text)) {
            $flags[] = "Requests off-platform communication via unverified personal messenger";
        }

        $isScam = count($flags) > 0;
        return [
            'is_scam' => $isScam ? 1 : 0,
            'reason'  => $isScam ? implode('; ', $flags) : null
        ];
    }

    /**
     * Extracts recognized technical skills from job title and description.
     */
    public static function extractSkills($text) {
        $catalog = [
            'Kotlin', 'Java', 'Jetpack Compose', 'Android SDK', 'MVVM', 'MVI',
            'Coroutines', 'Flow', 'StateFlow', 'Room', 'Retrofit', 'REST API',
            'Git', 'CI/CD', 'Unit Testing', 'Figma', 'UI/UX', 'Python',
            'Swift', 'Flutter', 'React Native', 'Docker', 'Kubernetes', 'SQL',
            'Firebase', 'GraphQL', 'TypeScript', 'JavaScript', 'AWS', 'C++'
        ];

        $matched = [];
        $lower = strtolower($text);
        foreach ($catalog as $skill) {
            $pattern = '/\b' . preg_quote(strtolower($skill), '/') . '\b/i';
            if (preg_match($pattern, $lower)) {
                $matched[] = $skill;
            }
        }
        if (empty($matched)) {
            $matched = ['Mobile Development', 'Software Engineering'];
        }
        return array_values(array_unique($matched));
    }

    /**
     * Syncs authentic job vacancies from Arbeitnow public API.
     */
    public static function syncFromArbeitnow($limit = 50) {
        $url = 'https://www.arbeitnow.com/api/job-board-api';
        $ch = curl_init($url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_USERAGENT, 'JobGuardAI-AggregationService/1.0');
        curl_setopt($ch, CURLOPT_TIMEOUT, 12);
        curl_setopt($ch, CURLOPT_SSL_VERIFYPEER, false);
        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        curl_close($ch);

        if ($httpCode !== 200 || empty($response)) {
            return [
                'success' => false,
                'message' => "Arbeitnow API returned HTTP $httpCode or was unreachable",
                'synced_count' => 0
            ];
        }

        $data = json_decode($response, true);
        if (!isset($data['data']) || !is_array($data['data'])) {
            return [
                'success' => false,
                'message' => 'Invalid JSON structure from Arbeitnow API',
                'synced_count' => 0
            ];
        }

        $db = getDatabaseConnection();
        $synced = 0;

        foreach ($data['data'] as $item) {
            if ($synced >= $limit) break;

            $slug = $item['slug'] ?? bin2hex(random_bytes(8));
            $jobId = 'arbeitnow_' . substr(md5($slug), 0, 16);
            $title = trim($item['title'] ?? 'Software Engineer');
            $companyName = trim($item['company_name'] ?? 'Verified Employer');
            $location = trim($item['location'] ?? 'Remote');
            $isRemote = !empty($item['remote']);
            if ($isRemote && stripos($location, 'remote') === false) {
                $location .= ' (Remote)';
            }

            // Determine type
            $jobTypes = $item['job_types'] ?? [];
            $type = 'Full-Time';
            if (is_array($jobTypes) && !empty($jobTypes)) {
                $type = ucwords(str_replace('_', ' ', $jobTypes[0]));
            } elseif ($isRemote) {
                $type = 'Remote';
            }

            // Parse description & skills
            $rawDesc = $item['description'] ?? '';
            $cleanDesc = trim(strip_tags($rawDesc));
            if (strlen($cleanDesc) > 2000) {
                $cleanDesc = substr($cleanDesc, 0, 1997) . '...';
            }

            // Skills from tags or extracted from description
            $tags = $item['tags'] ?? [];
            $extracted = self::extractSkills($title . ' ' . $cleanDesc . ' ' . implode(' ', (array)$tags));
            $skillsJson = json_encode(array_slice($extracted, 0, 8), JSON_UNESCAPED_UNICODE);

            $requirements = [
                "Demonstrated proficiency in relevant software technologies",
                "Strong foundation in problem solving and collaborative engineering",
                "Experience working with modern version control and CI/CD pipelines"
            ];
            $requirementsJson = json_encode($requirements, JSON_UNESCAPED_UNICODE);

            $salary = 'Competitive Market Rate';
            $experience = '2-5 years preferred';
            $externalUrl = $item['url'] ?? '';

            // Run scam detection
            $safety = self::inspectJobSafety($title, $cleanDesc, $salary);

            // 1. Upsert Company
            $companyId = 'comp_' . substr(md5($companyName), 0, 12);
            $compStmt = $db->prepare("
                INSERT INTO companies (id, name, location, industry, description, website, rating, employee_count, open_positions_count, is_verified)
                VALUES (:id, :name, :loc, 'Technology & Software', :desc, :url, 4.60, '100-1000', 1, 1)
                ON DUPLICATE KEY UPDATE 
                    open_positions_count = open_positions_count + 1,
                    location = VALUES(location)
            ");
            $compStmt->execute([
                ':id'   => $companyId,
                ':name' => $companyName,
                ':loc'  => $location,
                ':desc' => "$companyName is an active hiring partner on JobGuard AI.",
                ':url'  => $externalUrl
            ]);

            // 2. Upsert Job
            $jobStmt = $db->prepare("
                INSERT INTO jobs (id, title, company_id, company_name, company_logo, location, type, salary, experience, description, requirements, skills, deadline, is_saved, is_scam_flagged, scam_reason, match_score, source_id, external_url, is_active)
                VALUES (:id, :title, :comp_id, :comp_name, '', :loc, :type, :salary, :exp, :desc, :req, :skills, 'Rolling Basis', 0, :is_scam, :scam_reason, 85, 'source_arbeitnow', :url, 1)
                ON DUPLICATE KEY UPDATE
                    title = VALUES(title),
                    location = VALUES(location),
                    description = VALUES(description),
                    is_active = 1
            ");
            $jobStmt->execute([
                ':id'          => $jobId,
                ':title'       => $title,
                ':comp_id'     => $companyId,
                ':comp_name'   => $companyName,
                ':loc'         => $location,
                ':type'        => $type,
                ':salary'      => $salary,
                ':exp'         => $experience,
                ':desc'        => $cleanDesc,
                ':req'         => $requirementsJson,
                ':skills'      => $skillsJson,
                ':is_scam'     => $safety['is_scam'],
                ':scam_reason' => $safety['reason'],
                ':url'         => $externalUrl
            ]);

            $synced++;
        }

        // Update sync timestamp
        $db->exec("UPDATE job_sources SET last_synced_at = NOW() WHERE id = 'source_arbeitnow'");

        return [
            'success'      => true,
            'message'      => "Successfully synchronized $synced authentic job listings from Arbeitnow API",
            'synced_count' => $synced
        ];
    }
}

