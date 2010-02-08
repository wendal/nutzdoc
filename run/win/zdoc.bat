@echo off
SET JAVA_HOME=%JAVA_HOME%
SET NUTZ_HOME=C:\eclipse\workspaces\zozoh\nutz\bin
SET ZDOC_HOME=C:\eclipse\workspaces\zozoh\nutz.doc\bin

SET CLASSPATH=.;%JAVA_HOME%\lib\tools.jar;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\jre\rt.jar
SET CLASSPATH=%CLASSPATH%;%ZDOC_HOME%;%NUTZ_HOME%;
java org.nutz.doc.Doc %1 %2 %3 %4 %5
@echo on
