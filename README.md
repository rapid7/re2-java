re2-java
========

re2 for Java

Warning: Only 64bit Linux is supported for now. It should be easy to add support for other platforms.

## Licence ##

Like [RE2 library](http://code.google.com/p/re2/) iteself, this library can be distributed and used under the terms of [The BSD 3-Clause License](http://opensource.org/licenses/BSD-3-Clause).


## Installation ##

### Requirements ###
* Java 7 (JDK 1.7, never tested on Java 8). Set environment variable `JAVA_HOME` to point to the root directory of JDK.
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

### Changelog ###

#### v1.2

  - added `RE2.compile` static method, similar to `Pattern.compile`. The main difference with the `RE2` constructor
  is that `compile` method doesn't use checked exception and you can avoid `try/catch` block.

  - support for `RE2String` that can be reused with multiple patterns, in order to avoid multiple copies of the same string.

  - generalization of `RE2.matcher` that now accepts `CharSequence` rather than `String`

#### v1.1

 - support for `RE2Matcher`


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

`RE2` constructor is declared with checked exception that can be raised if the regex is malformed. This is quite annoying if
the regex is a static variable instantiated at startup. You can then use static method `RE2.compile` that wraps checked exception
to the unchecked `IllegalArgumentException`.

    public class MyClass {
        private static RE2 regex = RE2.compile("...");
    }

### Matcher ###

`RE2` object supports also a more javaesque interface, similar to `java.util.regex.Pattern` and `java.util.regex.Matcher`.

    RE2 re = new RE2("..(..)");
    RE2Matcher matcher = re.matcher("my input string");
    if (matcher.find()) {
      // get matching string(s),
      // see java.util.regex.Matcher javadoc or
      // com.logentries.re2.RE2Matcher code for additional details
      // eg. matcher.group(<n>) or matcher.start(<n>) and matcher.end(<n>)
      ...
    }

You can also iterate over the input string searching for repeated pattern

    RE2 re = new RE2("bla?");
    RE2Matcher matcher = re.matcher("my bla input string bl bla");
    while (matcher.findNext()) {
      // 3 iterations, get positions using matcher.start() and matcher.end()
    }

`R2Matcher` also implements `java.util.Iterable<java.util.regex.MatchResult>`.
It can be used this way

    int c = 0;
    for (MatchResult mr : new RE2("t").matcher("input text")) {
        // play with matches using mr.start, mr.end, mr.group
    }
    assertEquals(3, c);

This can be very useful when playing with this library in Scala:

    import scala.collection.JavaConversions._
    import com.logentries.re2._

    new RE2("abc?") matcher "abc and abc ab ab" map( _.group ) foreach println

If you are not interested in fetching groups offset you can disable this feature, by using

    RE2Matcher m = new RE2("ab(c?)").matcher("abc and abc ab ab", false);
    assertEquals(1, m.GroupCount());
    // now m contains information only for group 0
    // so m.start(), m.end() and m.group()
    // trying m.{start|end|group}(n : n > 0) always fails

If your regex is very complex (most likely programmatically composed by concatenating different patterns) and the
number of groups is huge, this can improve performance significantly (data structures to contain all possible matches
are not allocated).

**NOTE 1**: `RE2Matcher` object maintains a pointer to a char buffer that is used in C++ stack to manage the current string, in order to avoid a copy for each iteration.
For this reason, `RE2Matcher` object implements AutoCloseable interface, to be used in `try-with-resource` statement.
Close method is called in `finalize()`, so garbage collector will ensure (sooner or later) to free the memory. This is the same pattern that has been used for
`RE2` object, but, usually, `RE2` regex are compiled and then used multiple times while `RE2Matcher` objects
are used in stack and most likely you will want to delete it as soon as has been used.
In this case, you can use the `try-with-resource` block to make sure you don't miss anything

    try (RE2Matcher matcher = re.matcher("my bla input string bl bla")) {
      matcher. ....
    }

**NOTE 2**: `RE2Matcher` is not thread-safe, just like `java.util.regex.Matcher`

### Re-using strings ###

Whenever a `RE2Matcher` is created, the content of the string is copied to make it accessible from C++ stack. If you have to
check and search for several patterns on the same string, this could affect performances, because you are copying
the same string multiple times.

For this reason, from version v1.2, we have implemented a new object, `RE2String` that is a wrapper for a `CharSequence`.
You can create an instance of this object in advance, and then create a `RE2Matcher` using your `RE2String`. This new object
can be re-used multiple times to create matchers for different patterns.
When `RE2Matcher` is created using a `RE2String`, it doesn't copy the string and when you close it (see above about the `AutoCloseable` interface)
simply does nothing. Similarly, `RE2String` implements `AutoCloseable` interface and `finalize` method has been overridden to let the GC
clean resources for you.


    RE2 regex1 = RE2.compile("\\b[\\d]{5}\\b");
    RE2 regex2 = RE2.compile("\\b[a-zA-Z]{5}\\b");

    String input = ....
    RE2String rstring = new RE2String(input);

    RE2Matcher m1 = regex1.matcher(rstring);
    RE2Matcher m2 = regex2.matcher(rstring);
    while(m1.find()) {
        int endFirst = m1.end();
        if (m2.find(endFirst, endFirst + 10)) {
            ...
        }
    }

    // here m1.close() and m2.close() do nothing


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
~~If you have any idea how to implement it in different way, please give me know.~~ *See Matcher interface above*



### Options  ###

Object `com.logentries.re2.Options` encapsulates possible configuration that is used during creation of the RE2 object. It is more or less equivalent to RE2::Options
from C++ interface. It can be passed as a second argument to RE2 constructor.

It uses several setter methods to set the configuration values:

    Options opt = new Options();
    opt.setNeverNl(true);
    opt.setWordBoundary(false);

or equivalently:

    Options opt = new Options().setNeverNl(true).setWordBoundary(false);

`RE2` constructor is now overloaded to support for explicit flag list, to mimic C++ style:

        RE2 regex = new RE2("TGIF?",
            Options.CASE_INSENSITIVE,
            Options.ENCODING(Encoding.UTF8),
            Options.PERL_CLASSES(false)
        );

 see `Options` static fields for further details.