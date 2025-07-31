@echo off
REM Launch bundled MariaDB using the provided configuration
cd /d "%~dp0..\mariadb\bin"
mysqld --defaults-file=..\my.ini --console

