<?php
// =====================================================================
// JobGuard AI - Database Connection Configuration (PDO with Prepared Statements)
// =====================================================================

function loadEnv($envPath = null) {
    if (!$envPath) {
        $envPath = dirname(__DIR__) . '/.env';
    }
    if (!file_exists($envPath)) {
        return [];
    }
    $lines = file($envPath, FILE_IGNORE_NEW_LINES | FILE_SKIP_EMPTY_LINES);
    $env = [];
    foreach ($lines as $line) {
        $line = trim($line);
        if ($line === '' || strpos($line, '#') === 0) {
            continue;
        }
        $parts = explode('=', $line, 2);
        if (count($parts) === 2) {
            $key = trim($parts[0]);
            $val = trim($parts[1]);
            // Strip quotes if wrapped
            if (strlen($val) > 1 && (($val[0] === '"' && substr($val, -1) === '"') || ($val[0] === "'" && substr($val, -1) === "'"))) {
                $val = substr($val, 1, -1);
            }
            $env[$key] = $val;
            if (!isset($_ENV[$key])) {
                $_ENV[$key] = $val;
            }
        }
    }
    return $env;
}

function getDatabaseConnection() {
    static $pdoInstance = null;
    if ($pdoInstance !== null) {
        return $pdoInstance;
    }

    $env = loadEnv();
    $host = $env['DB_HOST'] ?? $_ENV['DB_HOST'] ?? '127.0.0.1';
    $port = $env['DB_PORT'] ?? $_ENV['DB_PORT'] ?? '3307';
    $db   = $env['DB_NAME'] ?? $_ENV['DB_NAME'] ?? 'jobguard_ai';
    $user = $env['DB_USER'] ?? $_ENV['DB_USER'] ?? 'root';
    $pass = $env['DB_PASS'] ?? $_ENV['DB_PASS'] ?? '';

    $dsn = "mysql:host={$host};port={$port};dbname={$db};charset=utf8mb4";
    $options = [
        PDO::ATTR_ERRMODE            => PDO::ERRMODE_EXCEPTION,
        PDO::ATTR_DEFAULT_FETCH_MODE => PDO::FETCH_ASSOC,
        PDO::ATTR_EMULATE_PREPARES   => false,
        PDO::MYSQL_ATTR_INIT_COMMAND => "SET NAMES utf8mb4 COLLATE utf8mb4_unicode_ci"
    ];

    try {
        $pdoInstance = new PDO($dsn, $user, $pass, $options);
        return $pdoInstance;
    } catch (PDOException $e) {
        // Fallback check: if port 3307 fails, attempt port 3306 gracefully
        if ($port == '3307') {
            try {
                $fallbackDsn = "mysql:host={$host};port=3306;dbname={$db};charset=utf8mb4";
                $pdoInstance = new PDO($fallbackDsn, $user, $pass, $options);
                return $pdoInstance;
            } catch (PDOException $ex) {
                // Return original error if fallback also fails
            }
        }
        http_response_code(500);
        header('Content-Type: application/json; charset=utf-8');
        echo json_encode([
            'success' => false,
            'status'  => 500,
            'message' => 'Database connection failed: ' . $e->getMessage()
        ]);
        exit;
    }
}

