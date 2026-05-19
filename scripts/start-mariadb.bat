@echo off
REM Launch bundled MariaDB using the provided configuration
cd /d "%~dp0..\mariadb"
if not exist "data\mysql" (
    echo Initializing MariaDB database...
    bin\mysqld --defaults-file=my.ini --initialize-insecure
)
echo Starting MariaDB server...
bin\mysqld --defaults-file=my.ini --init-file="%~dp0init.sql" --console
