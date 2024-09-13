echo off

set CHSLIB=./lib/*;./lib/lib/*

set CLASSPATH=.

set CLASSPATH=%CLASSPATH%;%CHSLIB%


C:\MentorGraphics\Capital21\jre\bin\java.exe -Xmx1024M ConverterCall %1 %2 %3

exit