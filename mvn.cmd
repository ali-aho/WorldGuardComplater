@echo off
set JAVA_HOME=C:\Users\ALI\vortexlink-build\tools\jdk-17.0.20.1+1
set PATH=%JAVA_HOME%\bin;%PATH%
call C:\Users\ALI\vortexlink-build\tools\apache-maven-3.9.11\bin\mvn.cmd %*
