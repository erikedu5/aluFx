@echo off
REM Launch bundled MariaDB using a user-writable local AppData directory to avoid Program Files permissions issues
set "DB_DATA_DIR=%LOCALAPPDATA%\aluFx\data"
set "DB_DATA_DIR=%DB_DATA_DIR:\=/%"

set "INIT_SQL=%~dp0init.sql"
set "INIT_SQL=%INIT_SQL:\=/%"

REM Asegurar que el directorio de datos existe
set "DB_DATA_DIR_WIN=%LOCALAPPDATA%\aluFx\data"
if not exist "%DB_DATA_DIR_WIN%" (
    echo Creating database data directory at "%DB_DATA_DIR_WIN%"...
    mkdir "%DB_DATA_DIR_WIN%"
)

cd /d "%~dp0..\mariadb"

REM Si la carpeta mysql no existe dentro del directorio de datos, inicializarla
if not exist "%DB_DATA_DIR_WIN%\mysql" (
    echo Initializing MariaDB database in "%DB_DATA_DIR%"...
    bin\mysqld --defaults-file=my.ini --datadir="%DB_DATA_DIR%" --log-error="%DB_DATA_DIR%/mysqld.err" --pid-file="%DB_DATA_DIR%/mysqld.pid" --initialize-insecure
)

echo Starting MariaDB server...
bin\mysqld --defaults-file=my.ini --datadir="%DB_DATA_DIR%" --log-error="%DB_DATA_DIR%/mysqld.err" --pid-file="%DB_DATA_DIR%/mysqld.pid" --init-file="%INIT_SQL%" --console
