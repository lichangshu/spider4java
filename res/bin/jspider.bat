@echo off
rem  $Id: jspider.bat,v 1.8 2003/04/22 16:43:33 vanrogu Exp $
rem
rem modify by changshu.li  2015.07.23
rem mail: lcs.005@163.com
rem
echo ------------------------------------------------------------
echo JSpider startup script

if exist "%JSPIDER_HOME%" goto JSPIDER_HOME_DIR_OK
  echo JSPIDER_HOME does not exist as a valid directory : %JSPIDER_HOME%
  echo Defaulting to current directory
  set JSPIDER_HOME=..

:JSPIDER_HOME_DIR_OK

echo JSPIDER_HOME=%JSPIDER_HOME%
echo ------------------------------------------------------------

set JSPIDER_OPTS=
set JSPIDER_OPTS=%JSPIDER_OPTS% -Djspider.home=%JSPIDER_HOME%
set JSPIDER_OPTS=%JSPIDER_OPTS% -Djava.util.logging.config.file=%JSPIDER_HOME%/common/conf/logging/logging.properties
set JSPIDER_OPTS=%JSPIDER_OPTS% -Dlog4j.configuration=conf/logging/log4j.xml

setlocal enabledelayedexpansion
set JSPIDER_CLASSPATH=%JSPIDER_HOME%
for /f %%i in ('dir /b "%JSPIDER_HOME%\lib\*.jar"') do (
  set JSPIDER_CLASSPATH=!JSPIDER_CLASSPATH!;%JSPIDER_HOME%\lib\%%i
)

set JSPIDER_CLASSPATH=%JSPIDER_CLASSPATH%;%JSPIDER_HOME%/common
set JSPIDER_CLASSPATH=%JSPIDER_CLASSPATH%;%CLASSPATH%

java -cp "%JSPIDER_CLASSPATH%;%classpath%" %JSPIDER_OPTS% net.javacoding.jspider.JSpider %1 %2
