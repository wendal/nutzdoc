@echo off
SET JAVA_HOME=D:\LANG\Java\JDK1.6
SET DEPS=D:\zzh\depen-jars
SET OUTPUT=D:\zzh\output
SET NUTZ=D:\workspace\galaxy\nutz@google\bin
SET PROJECT_HOME=D:\nutz\nutzdoc@google\trunk

D:
cd %PROJECT_HOME%\build
ant

@echo on

