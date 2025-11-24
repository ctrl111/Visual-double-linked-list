@echo off
echo ====================================
echo DataStructLab Kotlin Version
echo ====================================
echo.
echo Compiling and running the application...
echo.

call mvn clean compile
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo Compilation failed! Please check the errors above.
    pause
    exit /b 1
)

echo.
echo Starting JavaFX application...
echo.

call mvn javafx:run

pause

