re2-java
========

re2 for Java

Warning: Only 64bit Linux is supported for now. It should be easy to add support for other platforms.

## Installation ##

### Requirements ###
* Java 7 (JDK 1.7) or higher. Set environment variable `JAVA_HOME` to point to the root directory of JDK.
* Maven 3.x , http://maven.apache.org/ .
- Check that mvn command can be run from your command line.
* gcc 4.5.x or higher.
* Mercurial version control system. Make sure that command hg can be run from the command line.  
Note: Mercurial is used to download latest version of re2 sources.

### Compilation ###

Simply type:

    $ make

It downloads latest version of re2, builds re2 library in separate directory and builds so library with JNI bindigs too.
Finally, jar file that includes so libraries files is produced in the target folder.

You can type:

    $ make clean

to clean all files that comes inot existence during normal run of make.

### Instalation ###

After running of `make`, directory `target` contains jar file with the library. You can include it to your `classpath`.
Library files (libre2.so and libre2-java.so) are part of the jar file as well. They are extracted during JVM
startup, saved into temporary files and dynamically loaded into the address space of the JVM.
