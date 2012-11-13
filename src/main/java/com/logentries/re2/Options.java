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
}
