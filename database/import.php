<?php
try {
    $sqlFile = __DIR__ . '/jobguard_ai.sql';
    $sql = file_get_contents($sqlFile);
    
    // First ensure database exists
    $pdoInit = new PDO("mysql:host=127.0.0.1;port=3307;charset=utf8mb4", "root", "", [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION
    ]);
    $pdoInit->exec("CREATE DATABASE IF NOT EXISTS `jobguard_ai` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;");

    // Connect to jobguard_ai database
    $pdo = new PDO("mysql:host=127.0.0.1;port=3307;dbname=jobguard_ai;charset=utf8mb4", "root", "", [
        PDO::ATTR_ERRMODE => PDO::ERRMODE_EXCEPTION,
        PDO::MYSQL_ATTR_MULTI_STATEMENTS => true
    ]);
    
    $pdo->exec($sql);
    echo "SUCCESS: database/jobguard_ai.sql executed successfully!\n\n";
    
    $stmt = $pdo->query("SHOW TABLES;");
    $tables = $stmt->fetchAll(PDO::FETCH_COLUMN);
    echo "Tables in jobguard_ai (" . count($tables) . "):\n";
    foreach ($tables as $t) {
        echo " - " . $t . "\n";
    }
} catch (PDOException $e) {
    echo "ERROR importing database: " . $e->getMessage() . "\n";
}
