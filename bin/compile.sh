#!/usr/bin/env bash

. bin/common.sh

javac src/com/logentries/re2/RE2.java
javac src/com/logentries/re2/Options.java
javac src/com/logentries/re2/Encoding.java
javac src/com/logentries/re2/Main.java
javah -jni -o src/com/logentries/re2/RE2.h com.logentries.re2.RE2
javah -jni -o src/com/logentries/re2/Options.h com.logentries.re2.Options
g++ -O3 -g -fPIC $JDK_INCLUDES -Ire2 -c src/com/logentries/re2/RE2.cpp -o src/com/logentries/re2/RE2.o
g++ -shared -Wl,-soname,libre2-java.so -o src/com/logentries/re2/libre2-java.so src/com/logentries/re2/RE2.o -Lre2/obj/so -lre2 -lpthread

