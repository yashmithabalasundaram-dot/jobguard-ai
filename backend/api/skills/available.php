<?php
// =====================================================================
// JobGuard AI - Available Standard Skills Catalog API
// Endpoint: GET /api/skills/available.php
// =====================================================================

require_once dirname(__DIR__, 2) . '/config/cors.php';
handleCors();

require_once dirname(__DIR__, 2) . '/utils/response.php';

$standardSkills = [
    'Kotlin',
    'Jetpack Compose',
    'Android SDK',
    'MVVM',
    'Coroutines',
    'Room',
    'StateFlow',
    'Retrofit',
    'REST API',
    'Git',
    'CI/CD',
    'Unit Testing',
    'Figma',
    'UI/UX',
    'Java',
    'Python',
    'C++',
    'SQL'
];

jsonSuccess($standardSkills, 'Standard skills retrieved');

