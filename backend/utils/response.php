<?php
// =====================================================================
// JobGuard AI - Centralized JSON Response Handling
// =====================================================================

function jsonSuccess($data = null, $message = 'Success', $code = 200) {
    http_response_code($code);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode([
        'success' => true,
        'status'  => $code,
        'message' => $message,
        'data'    => $data
    ], JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    exit;
}

function jsonError($message = 'An error occurred', $code = 400, $errors = null) {
    http_response_code($code);
    header('Content-Type: application/json; charset=utf-8');
    $payload = [
        'success' => false,
        'status'  => $code,
        'message' => $message
    ];
    if ($errors !== null) {
        $payload['errors'] = $errors;
    }
    echo json_encode($payload, JSON_UNESCAPED_UNICODE | JSON_UNESCAPED_SLASHES);
    exit;
}

function getJsonInput() {
    $raw = file_get_contents('php://input');
    if (empty($raw)) {
        return $_POST;
    }
    $decoded = json_decode($raw, true);
    if (!is_array($decoded)) {
        return $_POST;
    }
    return array_merge($_POST, $decoded);
}

