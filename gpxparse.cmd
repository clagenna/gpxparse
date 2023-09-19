set LUOGO=%~dp0
cd /d "%LUOGO%"
cd
set qta=0
if "%JAVAFX_HOME%" == "" (
  call :errore "Non ha definito la variabile ambiente JAVAFX_HOME !"
  call :errore "Prova a lanciare settaEnvJava.ps1 come amministratore"
  goto fine
)

set JARTEST=target\gpxparse-1.0.jar
if exist "%JARTEST%" (
  set JAREXE=%JARTEST%
  call :info  "%JARTEST%"
  set /a qta=%qta%+1
  )

set JARTEST=target\gpxparse-1.0-SNAPSHOT.jar
if exist "%JARTEST%" (
  set JAREXE=%JARTEST%
  call :info  "%JARTEST%"
  set /a qta=%qta%+1
  )

set JARTEST=target\gpxparse-jar-with-dependencies.jar
if exist "%JARTEST%" (
  set JAREXE=%JARTEST%
  call :info  "%JARTEST%"
  set /a qta=%qta%+1
  )

set JARTEST=gpxparse-jar-with-dependencies.jar
if exist "%JARTEST%" (
  set JAREXE=%JARTEST%
  call :info  "%JARTEST%"
  set /a qta=%qta%+1
  )

set JARTEST=target\gpxparse.jar
if exist "%JARTEST%" (
  set JAREXE=%JARTEST%
  call :info  "%JARTEST%"
  set /a qta=%qta%+1
  )

set JARTEST=gpxparse.jar
if exist "%JARTEST%" (
  set JAREXE=%JARTEST%
  call :info  "%JARTEST%"
  set /a qta=%qta%+1
  )

set JARTEST=gpxparse-1.0.jar
if exist "%JARTEST%" (
  set JAREXE=%JARTEST%
  call :info  "%JARTEST%"
  set /a qta=%qta%+1
  )

if %qta% equ 0 (
  @echo.
  call :errore Non trovo il *.JAR del programma ?!?
  goto fine
) 
if %qta% gtr 1 (
  @echo.
  call :errore Troppi programmi *.JAR del programma !
  call :errore cancella le versioni piu vecchie
  call :errore e rilancia
  goto fine
) 
goto vai



:info
@echo off
echo [92m%*[0m
goto :eof

:errore
@echo off
echo [91m%*[0m
goto :eof

:vai
@echo on
set MODPATH=%JAVAFX_HOME%\lib
set MODS=javafx.controls
set MODS=%MODS%,javafx.base
set MODS=%MODS%,javafx.fxml
set MODS=%MODS%,javafx.graphics
rem set MODS=%MODS%,javafx.media

echo java --module-path "%MODPATH%" --add-modules="%MODS%" -jar "%JAREXE%"

:fine
