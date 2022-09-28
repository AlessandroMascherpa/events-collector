@echo off
setlocal

rem --- configuration ---
rem set EVENTS_COLLECTOR_REPOSITORY_TYPE=postgres
set EVENTS_COLLECTOR_REPOSITORY_URL=r2dbc:postgresql://postgres:password@localhost:5432/events-collector

rem --- look for the application jar file ---
set TARGET_DIRECTORY=.\server\target
set POM_PROPERTIES_FILE=%TARGET_DIRECTORY%\maven-archiver\pom.properties
IF EXIST "%POM_PROPERTIES_FILE%" GOTO ReadFile

echo The properties file %POM_PROPERTIES_FILE% does not exist.
echo Did you compile the project?
echo.

:ReadFile

for /F "tokens=2 delims==" %%a in ( 'findstr "artifactId"  %POM_PROPERTIES_FILE%' ) do set ARTIFACT_ID=%%a
for /F "tokens=2 delims==" %%a in ( 'findstr "version"  %POM_PROPERTIES_FILE%' ) do set ARTIFACT_VERSION=%%a

set  APPLICATION_NAME=%ARTIFACT_ID%-%ARTIFACT_VERSION%.jar
set  APPLICATION_FILE=%TARGET_DIRECTORY%\%APPLICATION_NAME%

IF EXIST "%APPLICATION_FILE%" GOTO RunApplication

echo The application file %APPLICATION_FILE% does not exist.
echo Did you compile the project?
echo.

:RunApplication

start  "events collector - server - %APPLICATION_NAME% - %EVENTS_COLLECTOR_REPOSITORY_TYPE%"  java  -jar %APPLICATION_FILE%

:eof
