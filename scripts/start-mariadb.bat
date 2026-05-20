@echo off
REM Launch bundled MariaDB using a user-writable local AppData directory to avoid Program Files permissions issues
set "DB_DATA_DIR_WIN=%LOCALAPPDATA%\aluFx\data"

set "INIT_SQL=%~dp0init.sql"
set "INIT_SQL=%INIT_SQL:\=/%"

REM Asegurar que el directorio de datos existe
if not exist "%DB_DATA_DIR_WIN%" (
    echo Creating database data directory at "%DB_DATA_DIR_WIN%"...
    mkdir "%DB_DATA_DIR_WIN%"
)

cd /d "%~dp0..\mariadb"

REM Si la carpeta mysql no existe dentro del directorio de datos, inicializarla usando la herramienta nativa de MariaDB
if not exist "%DB_DATA_DIR_WIN%\mysql" (
    echo Initializing MariaDB database in "%DB_DATA_DIR_WIN%"...
    bin\mysql_install_db.exe --datadir="%DB_DATA_DIR_WIN%"
    
    if errorlevel 1 (
        echo ❌ Database initialization failed!
        pause
        exit /b 1
    )
    echo DB initialized successfully.
    
    REM Esperar 2 segundos para asegurar que Windows libere todos los bloqueos de archivos tras la inicialización
    echo Waiting for filesystem locks to release...
    timeout /t 2 /nobreak >nul
)

echo Starting MariaDB server...
bin\mysqld --defaults-file=my.ini --datadir="%DB_DATA_DIR_WIN%" --log-error="%DB_DATA_DIR_WIN%\mysqld.err" --pid-file="%DB_DATA_DIR_WIN%\mysqld.pid" --init-file="%INIT_SQL%" --console
