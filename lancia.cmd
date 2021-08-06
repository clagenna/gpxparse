@echo off
java -version 
set aa=%errorlevel%
if %aa% equ 0 goto okjava
@echo.
@echo Non trovo Java installato !!!
@echo.
@echo Prova a visitare il sito (per java 16):
@echo   https://www.oracle.com/java/technologies/javase-jdk16-downloads.html
pause
goto fine

:okjava
set ESEG=gpxparse-jar-with-dependencies.jar
if exist "%ESEG%" goto lancia
set ESEG=gpxparse.jar
if exist "%ESEG%" goto lancia
set ESEG=target\gpxparse-jar-with-dependencies.jar
if exist "%ESEG%" goto lancia
set ESEG=gpxparse.jar
if exist "%ESEG%" goto lancia
@echo .
@echo Cannot find JAR to launch
goto fine

:lancia
java -jar "%ESEG%"

:fine
