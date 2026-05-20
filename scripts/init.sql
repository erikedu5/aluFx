CREATE DATABASE IF NOT EXISTS aluhelper CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'alufx_user'@'localhost' IDENTIFIED BY 'root';
ALTER USER 'alufx_user'@'localhost' IDENTIFIED BY 'root';

GRANT ALL PRIVILEGES ON aluhelper.* TO 'alufx_user'@'localhost';

FLUSH PRIVILEGES;
