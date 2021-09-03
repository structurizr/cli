@echo off
setlocal
set SCRIPT_DIR=%~dp0
java -cp "%SCRIPT_DIR%;%SCRIPT_DIR%\lib\*;" com.structurizr.cli.StructurizrCliApplication %*
