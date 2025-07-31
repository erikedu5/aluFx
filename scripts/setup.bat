@echo off
echo Ejecutando configuración inicial de MySQL...

REM Cambia esto a tu ruta de instalación MySQL local si es necesario
set MYSQL_BIN="C:\Program Files\MySQL\MySQL Server 8.0\bin"

REM Ejecuta script SQL que creará DB/usuarios/tablas
%MYSQL_BIN%\mysql -u root -proot -e "CREATE DATABASE IF NOT EXISTS alufx;"
