@echo off
echo ========================================
echo   Hospital Appointment System
echo   PROJECT CLEANUP SCRIPT
echo ========================================
echo.
echo This script will delete unnecessary files from the project.
echo Please read PROJECT_AUDIT_REPORT.md before proceeding.
echo.
pause

echo.
echo [1/4] Deleting documentation files...
del /F /Q APPLICATION_RUNNING_NOW.md 2>nul
del /F /Q APP_RUNNING_STATUS.md 2>nul
del /F /Q CLEANUP_REPORT.md 2>nul
del /F /Q CONFIGURATION_SUMMARY.md 2>nul
del /F /Q DASHBOARD_FIX.md 2>nul
del /F /Q DATABASE_STATUS.md 2>nul
del /F /Q DEBUG_MODE.md 2>nul
del /F /Q LOGIN_FIXED.md 2>nul
del /F /Q PLEASE_READ.md 2>nul
del /F /Q PROPERTIES_FILES_EXPLAINED.md 2>nul
del /F /Q PROPERTIES_SIMPLIFIED.md 2>nul
del /F /Q REBUILD_STATUS.md 2>nul
del /F /Q RESTART_SUMMARY.md 2>nul
del /F /Q SIMPLE_SOLUTION.md 2>nul
del /F /Q STARTUP_TROUBLESHOOTING.md 2>nul
echo Done!

echo.
echo [2/4] Deleting SQL script files...
del /F /Q CHECK_DATA.sql 2>nul
del /F /Q CHECK_DATABASE.sql 2>nul
del /F /Q CREATE_SAMPLE_USERS.sql 2>nul
del /F /Q FINAL_USER_FIX.sql 2>nul
del /F /Q QUICK_FIX_USERS.sql 2>nul
del /F /Q REMOVE_DEFAULT_USERS.sql 2>nul
del /F /Q setup_users.sql 2>nul
del /F /Q UPDATE_USER_ROLES.sql 2>nul
del /F /Q FRESH_START.sql 2>nul
echo Done!

echo.
echo [3/4] Deleting batch script files...
del /F /Q CLEANUP_PROJECT.bat 2>nul
del /F /Q CREATE_DATABASE.bat 2>nul
del /F /Q DATABASE_SETUP.bat 2>nul
del /F /Q FINAL_AUTO_FIX.bat 2>nul
del /F /Q FINAL_RUN.bat 2>nul
del /F /Q FRESH_START.bat 2>nul
del /F /Q RUN_WITH_PASSWORD.bat 2>nul
del /F /Q SAVE_AND_RUN.bat 2>nul
echo Done!

echo.
echo [4/4] Deleting empty/test files...
del /F /Q src\main\java\com\example\hospital\controller\TestController.java 2>nul
del /F /Q src\main\resources\templates\test_users.html 2>nul
echo Done!

echo.
echo ========================================
echo   CLEANUP COMPLETE!
echo ========================================
echo.
echo Files removed successfully.
echo.
echo IMPORTANT: You still need to manually:
echo   1. Clean up pom.xml (remove unused dependencies)
echo   2. Clean up application.properties (remove unused configs)
echo   3. Delete unused config files (see PROJECT_AUDIT_REPORT.md)
echo.
echo To run the project, use:
echo   mvnw.cmd spring-boot:run
echo.
pause
