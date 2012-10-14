#!/usr/bin/env bash

. bin/common.sh

javac -Xlint:unchecked test/com/logentries/re2_test/GenRegExpr.java &&
javac -Xlint:unchecked test/com/logentries/re2_test/GenString.java &&
javac -Xlint:unchecked test/com/logentries/re2_test/TestRandomExpr.java &&
javac test/com/logentries/re2_test/TestThreads.java &&
javac test/com/logentries/re2_test/Main.java &&
java com.logentries.re2_test.Main &&
echo "Everything Works" &&
true

