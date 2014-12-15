/*
 *      Java Bindings for the RE2 Library
 *
 *      (c) 2012 Daniel Fiala <danfiala@ucw.cz>
 *
 */

package com.logentries.re2;

public final class Options extends LibraryLoader {
    private Encoding encoding;
    private boolean posixSyntax;
    private boolean longestMatch;
    private boolean logErrors;
    private long maxMem;
    private boolean literal;
    private boolean neverNl;
    private boolean neverCapture;
    private boolean caseSensitive;
    private boolean perlClasses;
    private boolean wordBoundary;
    private boolean oneLine;

    private native void setDefaults();

    public Options() {
        setDefaults();
    }

    public Options setEncoding(final Encoding encoding) {
        this.encoding = encoding;
        return this;
    }
    public Options setPosixSyntax(final boolean posixSyntax) {
        this.posixSyntax = posixSyntax;
        return this;
    }
    public Options setLongestMatch(final boolean longestMatch) {
        this.longestMatch = longestMatch;
        return this;
    }
    public Options setLogErrors(final boolean logErrors) {
        this.logErrors = logErrors;
        return this;
    }
    public Options setMaxMem(final long maxMem) {
        this.maxMem = maxMem;
        return this;
    }
    public Options setLiteral(final boolean literal) {
        this.literal = literal;
        return this;
    }
    public Options setNeverNl(final boolean neverNl) {
        this.neverNl = neverNl;
        return this;
    }
    public Options setNeverCapture(final boolean neverCapture) {
        this.neverCapture = neverCapture;
        return this;
    }
    public Options setCaseSensitive(final boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
        return this;
    }
    public Options setCaseInsensitive(final boolean caseInsensitive) {
        this.caseSensitive = !caseInsensitive;
        return this;
    }
    public Options setPerlClasses(final boolean perlClasses) {
        this.perlClasses = perlClasses;
        return this;
    }
    public Options setWordBoundary(final boolean wordBoundary) {
        this.wordBoundary = wordBoundary;
        return this;
    }
    public Options setOneLine(final boolean oneLine) {
        this.oneLine = oneLine;
        return this;
    }

    /// FLAGS
    public static interface Flag {
        public void apply(Options opt);
    }
    
    public static final Flag POSIX_SINTAX = POSIX_SINTAX(true);
    public static Flag POSIX_SINTAX(final boolean v) {
        return new Flag() {
            @Override
            public void apply(Options opt) {
                opt.setPosixSyntax(v);
            }
        };
    }
    public static final Flag LONGEST_MATCH = LONGEST_MATCH (true);
	public static Flag LONGEST_MATCH(final boolean v) {
        return new Flag() {
            @Override
            public void apply(Options opt) {
                opt.setLongestMatch(v);
            }
        };
    }
    public static final Flag LOG_ERRORS = LOG_ERRORS (true);
	public static Flag LOG_ERRORS(final boolean v) {
        return new Flag() {
            @Override
            public void apply(Options opt) {
                opt.setLogErrors(v);
            }
        };
    }
    public static final Flag LITERAL = LITERAL (true);
	public static Flag LITERAL(final boolean v) {
        return new Flag() {
            @Override
            public void apply(Options opt) {
                opt.setLiteral(v);
            }
        };
    }
    public static final Flag NEVER_NL = NEVER_NL (true);
	public static Flag NEVER_NL(final boolean v) {
        return new Flag() {
            @Override
            public void apply(Options opt) {
                opt.setNeverNl(v);
            }
        };
    }
    public static final Flag NEVER_CAPTURE = NEVER_CAPTURE (true);
	public static Flag NEVER_CAPTURE(final boolean v) {
        return new Flag() {
            @Override
            public void apply(Options opt) {
                opt.setNeverCapture(v);
            }
        };
    }
    public static final Flag CASE_SENSITIVE = CASE_SENSITIVE (true);
	public static Flag CASE_SENSITIVE(final boolean v) {
        return new Flag() {
            @Override
            public void apply(Options opt) {
                opt.setCaseSensitive(v);
            }
        };
    }
    public static final Flag CASE_INSENSITIVE = CASE_INSENSITIVE (true);
	public static Flag CASE_INSENSITIVE(final boolean v) {
        return new Flag() {
            @Override
            public void apply(Options opt) {
                opt.setCaseInsensitive(v);
            }
        };
    }
    public static final Flag PERL_CLASSES = PERL_CLASSES (true);
	public static Flag PERL_CLASSES(final boolean v) {
        return new Flag() {
            @Override
            public void apply(Options opt) {
                opt.setPerlClasses(v);
            }
        };
    }
    public static final Flag WORD_BOUNDARY = WORD_BOUNDARY (true);
	public static Flag WORD_BOUNDARY(final boolean v) {
        return new Flag() {
            @Override
            public void apply(Options opt) {
                opt.setWordBoundary(v);
            }
        };
    }
    public static final Flag ONE_LINE = ONE_LINE (true);
	public static Flag ONE_LINE(final boolean v) {
        return new Flag() {
            @Override
            public void apply(Options opt) {
                opt.setOneLine(v);
            }
        };
    }

    public static Flag MAX_MEMORY(final long m) {
        return new Flag() {
            @Override
            public void apply(Options opt) {
                opt.setMaxMem(m);
            }
        };
    }

    public static final Flag UTF8_ENCODING = ENCODING(Encoding.UTF8);
    public static final Flag LATIN1_ENCODING = ENCODING(Encoding.Latin1);
    public static Flag ENCODING(final Encoding e) {
        return new Flag() {
            @Override
            public void apply(Options opt) {
                opt.setEncoding(e);
            }
        };
    }


}
