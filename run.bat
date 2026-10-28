@echo off
REM ------------------------------------------------------------
REM  Compiles and runs the Console Banking System.
REM  Double-click this file, or run it from the VS Code terminal.
REM ------------------------------------------------------------
setlocal

REM Always work from the folder that contains this file, so that
REM transactions.txt is created next to run.bat and src.
cd /d "%~dp0"

where javac >nul 2>nul
if errorlevel 1 (
    echo.
    echo ERROR: Java JDK was not found ^(the "javac" command is missing^).
    echo.
    echo What to do:
    echo   1. Install a JDK, version 17 or newer, for example Eclipse Temurin from adoptium.net
    echo   2. During installation, enable the options that add Java to PATH.
    echo   3. Close this window and VS Code completely, open them again, then run run.bat again.
    echo   4. To check, open a terminal and type:  javac -version
    echo.
    pause
    exit /b 1
)

where java >nul 2>nul
if errorlevel 1 (
    echo.
    echo ERROR: The "java" command was not found. Install a JDK ^(17 or newer^) and add it to PATH.
    echo.
    pause
    exit /b 1
)

if not exist bin mkdir bin

echo Compiling...
javac -d bin src\banking\*.java
if errorlevel 1 (
    echo.
    echo ERROR: Compilation failed. Read the messages above, fix the Java file, and run again.
    echo.
    pause
    exit /b 1
)

echo Compilation successful. Starting the Console Banking System...
echo.
java -cp bin banking.BankingSystemMain

echo.
echo Program finished. Records are saved in transactions.txt
pause
endlocal
