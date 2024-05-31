@echo off
set GRADLE_VERSION=8.6
set TEMP_DIR=%~dp0gradle_temp
set WRAPPER_DIR=%~dp0gradle\wrapper

echo Creating temporary directory...
mkdir %TEMP_DIR%

echo Downloading Gradle %GRADLE_VERSION%...
curl -Lo %TEMP_DIR%\gradle.zip https://services.gradle.org/distributions/gradle-%GRADLE_VERSION%-bin.zip

echo Extracting Gradle...
tar -xf %TEMP_DIR%\gradle.zip -C %TEMP_DIR%

echo Generating wrapper files...
%TEMP_DIR%\gradle-%GRADLE_VERSION%\bin\gradle wrapper --gradle-version %GRADLE_VERSION%

echo Cleaning up...
rmdir /s /q %TEMP_DIR%

echo Done.
pause
