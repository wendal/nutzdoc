@echo off
SET JAVA_HOME=D:\LANG\Java\JDK1.6
SET DEPS=D:\zzh\nutzam-deps
SET OUTPUT=D:\zzh\output
SET PROJECT_HOME=D:\nutz\nutzam@google\trunk

D:
cd %PROJECT_HOME%\build
ant

@echo on

