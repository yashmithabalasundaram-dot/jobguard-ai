<?php
// =====================================================================
// JobGuard AI - Input Validation & Sanitization Helper
// =====================================================================

function validateEmail($email) {
    return filter_var($email, FILTER_VALIDATE_EMAIL) !== false;
}

function sanitizeString($str) {
    if ($str === null) return '';
    return htmlspecialchars(strip_tags(trim($str)), ENT_QUOTES, 'UTF-8');
}

function requireFields($data, array $required) {
    $missing = [];
    foreach ($required as $field) {
        if (!isset($data[$field]) || trim((string)$data[$field]) === '') {
            $missing[] = $field;
        }
    }
    return $missing;
}

