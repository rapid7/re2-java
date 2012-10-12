#!/usr/bin/env bash

. bin/common.sh

javac test/com/logentries/re2_test/TestThreads.java &&
javac test/com/logentries/re2_test/Main.java &&
java com.logentries.re2_test.Main &&
echo "Everything Works" &&
true

