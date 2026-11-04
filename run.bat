@echo off
title TP-155-Spring-IoC-OCP
chcp 65001 >nul
mode con: cols=120 lines=55
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "MAVEN_OPTS=-Dfile.encoding=UTF-8"
set "PATH=%JAVA_HOME%\bin;%PATH%"
cd /d "%~dp0"

:menu
cls
echo ==============================================================================
echo   TP 3 - Spring IoC et principe Ouvert/Ferme
echo   Selection d'implementation de DAO sans modifier le code metier
echo   Spring 5.3.30  /  JDK 17  /  Maven
echo ==============================================================================
echo.
echo   [1] Demonstration complete (les 7 etapes du TP)
echo   [2] Suite de tests unitaires (mvn test)
echo   [3] Recompiler proprement le projet
echo   [0] Quitter
echo.
set "choix="
set /p "choix=Votre choix : "
if "%choix%"=="1" goto :demo
if "%choix%"=="2" goto :tests
if "%choix%"=="3" goto :compile
if "%choix%"=="0" goto :fin
goto :menu

:demo
cls
echo Compilation puis execution de la demonstration...
echo.
call mvn -B -q clean compile
if errorlevel 1 goto :echec
call mvn -B -q exec:java -Dexec.mainClass=Main
if errorlevel 1 goto :echec
goto :suspension

:tests
cls
echo Execution de la suite de tests...
echo.
call mvn -B test
if errorlevel 1 goto :echec
goto :suspension

:compile
cls
call mvn -B -q clean compile
if errorlevel 1 goto :echec
echo.
echo Compilation terminee sans erreur.
goto :suspension

:echec
echo.
echo *** Echec de la compilation ou de l'execution. ***
:suspension
echo.
echo ==============================================================================
echo   Fin de l'operation. Appuyez sur une touche pour revenir au menu.
echo ==============================================================================
pause >nul
goto :menu

:fin
exit /b 0
