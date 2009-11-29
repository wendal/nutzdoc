@echo off
SET JAVA_HOME=C:\Program Files\Java\jdk1.6.0_16
SET NUTZ=D:\home\zozoh\workspace\galaxy\nutz@google\bin
SET DEPS=D:\home\nutz\deps
SET OUTPUT=D:\home\nutz\jars
SET PROJECT_HOME=D:\home\zozoh\workspace\svn\google.nutzdoc\trunk

D:
cd %PROJECT_HOME%\build
ant
@echo on
