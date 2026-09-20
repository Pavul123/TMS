@echo off
echo ===================================================
echo   Starting TransFlow TMS (Frontend + Backend)
echo ===================================================

echo [1/2] Starting Spring Boot Backend (Port 8080)...
start "TMS Backend (Port 8080)" cmd /k "cd /d %~dp0tms-backend && ..\tools\apache-maven-3.9.6\bin\mvn.cmd spring-boot:run"

echo [2/2] Starting Next.js Frontend (Port 3000)...
start "TMS Frontend (Port 3000)" cmd /k "cd /d %~dp0TMS && npm run dev"

echo.
echo ===================================================
echo   TransFlow TMS is launching!
echo   - Frontend: http://localhost:3000
echo   - Swagger API Docs: http://localhost:8080/swagger-ui/index.html
echo ===================================================
timeout /t 5
start http://localhost:3000
