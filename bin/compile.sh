#!/usr/bin/env bash

. bin/common.sh

javac src/com/logentries/re2/RE2.java
javac src/com/logentries/re2/Options.java
javac src/com/logentries/re2/Encoding.java
javac src/com/logentries/re2/Main.java
javah -jni -o src/com/logentries/re2/RE2.h com.logentries.re2.RE2
javah -jni -o src/com/logentries/re2/Options.h com.logentries.re2.Options
g++ -O3 -g -fPIC $JDK_INCLUDES -Ire2 -c src/com/logentries/re2/RE2.cpp -o src/com/logentries/re2/RE2.o
#g++ -O3 -fPIC -I$JDK/include -I$JDK/include/linux -I../re2 -c com/logentries/re2/Options.cpp -o com/logentries/re2/Options.o
#g++ -shared -Wl,-soname,libre2-java.so -o com/logentries/re2/libre2-java.so com/logentries/re2/{RE2,Options}.o -L../re2/obj/so -lre2 -lpthread
g++ -shared -Wl,-soname,libre2-java.so -o src/com/logentries/re2/libre2-java.so src/com/logentries/re2/RE2.o -Lre2/obj/so -lre2 -lpthread

