re2-java
========

re2 for Java

Warning: Only 64bit Linux is supported for now. It should be easy to add support for other platforms.

## Licence ##

Like [RE2 library](http://code.google.com/p/re2/) iteself, this library can be distributed and used under the terms of [The BSD 3-Clause License](http://opensource.org/licenses/BSD-3-Clause).


## Installation ##

### Requirements ###
* Java 6 (JDK 1.6) or higher. Set environment variable `JAVA_HOME` to point to the root directory of JDK.
  I really like try-with-resources statement from Java 7, but on the other hand, Java 7 seems not to be stable enough so far.
* Maven 3.x , http://maven.apache.org/ .
- Check that mvn command can be run from your command line.
* gcc 4.5.x or higher.
* Boost C++ Library (http://www.boost.org/), version newer than Stonehenge should be enough.
* wget

### Compilation ###

Simply type:

    $ make

It downloads latest stable revision of re2, builds re2 library in separate directory and builds another library with JNI bindigs as well.
Finally, jar file that includes so libraries files is produced in the target folder.

You can type:

    $ make clean

to clean all files that come into existence during normal run of make.

After seccessfull compilation you can run:

    $ mvn test

But tests are very time and memory consuming and, at present, they print a lot of debug messages. Sorry if it is annoying,
this binding is actually under development.

### Installation ###

After running of `make`, directory `target` contains jar file with the library. You can include it to your `classpath`.
Native library files (libre2.so and libre2-java.so) are part of the jar file as well. They are extracted after JVM
startup, saved into temporary files and dynamically loaded into the address space of the JVM.

## Usage ##

For usage of the library, please import `com.logentries.re2.RE2` and `com.logentries.re2.Options` .

Basic usage of java-re2 is quite similar to the C++ library.

Static functions `RE2.fullMatch(.)` and `RE2.partialMatch(.)` can be used.

You can create precompiled RE in this way:

    RE2 re = new RE2("\\d+");

as the object allocates some memory that is not under the control of JVM, it should be freed explicitly.
You can either use member function `dispoze()`, or member function `close()` .
Class RE2 contains overloaded method `finalize()` that is automatically called before the object is destroyed by the Garbage Collector.
This method ensures that the additional memory is freed and may be frees it on its own.
But it is usually bad idea to rely on Java GC. :-)

Any try to use the object after the call of `dispoze()` or `close()` will cause the thrown of `IllegalStateException` .

Precompiled RE supports member functions `partialMatch(.)` or `fullMatch(.)`.

    re.fullMatch("2569");
    re.partialMatch("xxx=2569");

### Submatch extraction ###

Both static and member match functions support additional parameters in which submatches will be stored.
Java does not support passing arguments by reference, so we use arrays to store submatches:

    int[] x = new int[1];
    long[] y = new int[1];
    RE2.fullMatch("121:2569856321142", "(\\d+):(\\d+)", x, y);
    // x[0] == 121, y[0] == 2569856321142

Array of length bigger then 1 can be used. Then it is used to store as much consecutive submatches as is the length of the array:

    int[] x = new int[2];
    String[] s = new String[1];
    long[] y = new long[3];
    new RE2 re = new RE2("(\\d+):(\\d+)-([a-zA-Z]+)-(\\d+):(\\d+):(\\d+)");
    re.fullMatch("225:3-xxx-2:2555422298777:7", x, s, y);
    // x[0] == 225, x[1] == 3, s[0] == xxx, y[0] == 2, y[1] == 2555422298777, y[2] == 7

So far, only int[], long[], float[], double[] and String[] are supported. Adding of other types should be quite easy.

### Little comment about the interface and passing by reference ###

I know that a lot of Java programmers may complain that the interface based on passing of parameters by reference through the trick with arrays
is quite bad practise, dirty trick and that it introduces something what is in fact not present in Java.

But after I try it in a real code I decided that it is the best way to pass the values of submatches.
If you have any idea how to implement it in different way, please give me know.

### Options  ###

Object `com.logentries.re2.Options` encapsulates possible configuration that is used during creation of the RE2 object. It is more or less equivalent to RE2::Options
from C++ interface. It can be passed as a second argument to RE2 constructor.

It uses several setter methods to set the configuration values:

    Options opt = new Options();
    opt.setNeverNl(true);
    opt.setWordBoundary(false);

or equivalently:

    Options opt = new Options().setNeverNl(true).setWordBoundary(false);

etc.

C++ interface contains automatic conversion from some options to RE2::Options. For example you can write (in C++):

    RE2 re("(ab", RE2::Quiet);

It cannot be done in Java, instead you should write:

    RE2 re2 = new RE2("Ourobor+os", new Options().setQuiet(true));
    ...
