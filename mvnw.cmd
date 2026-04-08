@ECHO OFF
SETLOCAL

SET MAVEN_PROJECTBASEDIR=%~dp0
SET WRAPPER_DIR=%MAVEN_PROJECTBASEDIR%\.mvn\wrapper
SET WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar

IF NOT EXIST "%WRAPPER_JAR%" (
  powershell -NoProfile -ExecutionPolicy Bypass -Command "& {$ErrorActionPreference='Stop'; New-Item -ItemType Directory -Force -Path '%WRAPPER_DIR%' | Out-Null; Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.3.2/maven-wrapper-3.3.2.jar' -OutFile '%WRAPPER_JAR%'}"
)

IF NOT EXIST "%WRAPPER_JAR%" (
  ECHO Failed to download Maven wrapper jar.
  EXIT /B 1
)

java -classpath "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %*