#! /bin/sh

javac -d bin -sourcepath src/main -cp "lib/poi-3.11/*" src/main/core/MainConsole.java 

java -cp "lib/poi-3.11/*:bin" core.MainConsole 1 1 1 0

