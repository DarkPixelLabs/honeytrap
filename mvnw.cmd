@echo off
where mvn >nul 2>nul
if %ERRORLEVEL% EQU 0 (mvn %*) else (echo Maven is required. Install Maven or use the Docker build. & exit /b 1)
