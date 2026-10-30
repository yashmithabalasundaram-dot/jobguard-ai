<?php
// =====================================================================
// JobGuard AI - Authentication Middleware
// Protects private REST API endpoints using secure Bearer tokens
// =====================================================================

require_once dirname(__DIR__) . '/config/database.php';
require_once dirname(__DIR__) . '/utils/response.php';

function getBearerToken() {
    $header = null;
    if (isset($_SERVER['HTTP_AUTHORIZATION'])) {
        $header = trim($_SERVER['HTTP_AUTHORIZATION']);
    } elseif (isset($_SERVER['Authorization'])) {
        $header = trim($_SERVER['Authorization']);
    } elseif (function_exists('apache_request_headers')) {
        $requestHeaders = apache_request_headers();
        $requestHeaders = array_combine(array_map('ucwords', array_keys($requestHeaders)), array_values($requestHeaders));
        if (isset($requestHeaders['Authorization'])) {
            $header = trim($requestHeaders['Authorization']);
        }
    }

    if (!empty($header)) {
        if (preg_match('/Bearer\s(\S+)/i', $header, $matches)) {
            return $matches[1];
        }
    }

    // Fallback query parameter for media/file requests if needed
    if (!empty($_GET['token'])) {
        return trim($_GET['token']);
    }

    return null;
}

function requireAuth() {
    $token = getBearerToken();
    if (empty($token)) {
        jsonError('Authentication required: Missing Authorization Bearer header', 401);
    }

    $db = getDatabaseConnection();
    $stmt = $db->prepare("
        SELECT id, full_name, email, phone, is_verified, token_expires_at 
        FROM users 
        WHERE auth_token = :token AND (token_expires_at IS NULL OR token_expires_at > NOW())
        LIMIT 1
    ");
    $stmt->execute([':token' => $token]);
    $user = $stmt->fetch();

    if (!$user) {
        jsonError('Unauthorized: Invalid or expired session token', 401);
    }

    return $user;
}

