CREATE DATABASE waiting_local CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'waiting_app'@'localhost' IDENTIFIED BY 'waiting_password';
CREATE USER 'waiting_app'@'%' IDENTIFIED BY 'waiting_password';
GRANT ALL ON waiting_local.* to 'waiting_app'@'localhost';
GRANT ALL ON waiting_local.* to 'waiting_app'@'%';
