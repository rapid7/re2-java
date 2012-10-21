re2-java
========

re2 for Java

Warning: Only 64bit Linux is supported for now. It should be easy to add other platforms.

## Installation ##

### Requirements ###
* Java 7 (JDK 1.7) or higher.
* Maven 3.x , http://maven.apache.org/ .
- Check that mvn command can be run from your command line.
* gcc 4.5.x or higher.
* Mercurial version control system. Make sure that command hg can be run from the command line.  
Note: Mercurial is used to download latest version of re2 sources.

### Compilation ###

Simply type:

    $ make

It downloads latest version of re2, builds re2 library in separate directory and builds so library with JNI bindigs too.
Finally jar file that includes so libraries files is produced in the target folder.
