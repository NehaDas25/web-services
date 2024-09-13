@echo off

rem Define the base directory
set BASE_DIR=%~dp0
set SERVER_LIB=%BASE_DIR%lib
set CLASSES_DIR=%BASE_DIR%classes

rem Initialize the classpath with classes directory
set CLASSPATH=%CLASSES_DIR%

rem Enable delayed variable expansion
setlocal enabledelayedexpansion

rem Include all JAR files from SERVER_LIB in the classpath
for %%f in ("%SERVER_LIB%\*.jar") do (
    set CLASSPATH=!CLASSPATH!;%%f
)

rem Output current classpath for debugging
echo Current CLASSPATH: %CLASSPATH%

echo BASE_DIR: %BASE_DIR%
echo SERVER_LIB: %SERVER_LIB%
echo CLASSES_DIR: %CLASSES_DIR%

rem Run the Java application
java.exe -Xrs -Xmx30000M -XX:-UseGCOverheadLimit -XX:MaxHeapFreeRatio=20 -XX:MinHeapFreeRatio=10 -cp "!CLASSPATH!" --add-exports java.xml/com.sun.org.apache.xerces.internal.dom=ALL-UNNAMED --add-exports java.xml/com.sun.org.apache.xerces.internal.jaxp=ALL-UNNAMED com.mentor.soapProcess.WebServ 3113 1000 1> log_server.txt 2>&1

rem Check the exit code and handle errors
if %errorlevel% neq 0 (
    echo Java application exited with error code %errorlevel%
    pause
)

endlocal
