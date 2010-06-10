@echo off
SET JAVA_HOME=%JAVA_HOME%
SET NUTZ_HOME=D:\workspace\galaxy\nutz@google\bin
SET ZDOC_HOME=D:\workspace\galaxy\nutzdoc@google\bin
SET ITEXT_CORE=D:\3X\iText\2.1.4\iText-2.1.4.jar
SET ITEXT_Asian=D:\3X\iText\2.1.4\iTextAsian.jar
SET ITEXT_FONT=D:\3X\iText\font

SET CLASSPATH=.;%JAVA_HOME%\lib\tools.jar;%JAVA_HOME%\lib\dt.jar;%JAVA_HOME%\jre\rt.jar
SET CLASSPATH=%CLASSPATH%;%ITEXT_CORE%;
SET CLASSPATH=%CLASSPATH%;%ITEXT_Asian%;
SET CLASSPATH=%CLASSPATH%;%ITEXT_FONT%;
SET CLASSPATH=%CLASSPATH%;%ZDOC_HOME%;
SET CLASSPATH=%CLASSPATH%;%NUTZ_HOME%;

java org.nutz.doc.Doc %1 %2 %3 %4 %5 %6 %7 %8 %9
@echo on
