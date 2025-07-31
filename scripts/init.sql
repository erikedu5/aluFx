CREATE DATABASE IF NOT EXISTS alufx CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'alufx_user'@'localhost' IDENTIFIED BY 'root';

GRANT ALL PRIVILEGES ON alufx.* TO 'alufx_user'@'localhost';

FLUSH PRIVILEGES;
