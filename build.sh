#!/usr/bin/env bash

PATH=$JDK/bin/:$PATH

javac com/logentries/re2/RE2.java
javac com/logentries/re2/Main.java
javah -classpath . -jni -o com/logentries/re2/RE2.h com.logentries.re2.RE2
javah -classpath . -jni -o com/logentries/re2/Options.h com.logentries.re2.Options
g++ -O3 -fPIC -I$JDK/include -I$JDK/include/linux -I../re2 -c com/logentries/re2/RE2.cpp -o com/logentries/re2/RE2.o
g++ -O3 -fPIC -I$JDK/include -I$JDK/include/linux -I../re2 -c com/logentries/re2/Options.cpp -o com/logentries/re2/Options.o
g++ -shared -Wl,-soname,libre2-java.so -o com/logentries/re2/libre2-java.so com/logentries/re2/{RE2,Options}.o -L../re2/obj/so -lre2 -lpthread
