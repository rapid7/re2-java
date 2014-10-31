/*
 *      Java Bindings for the RE2 Library
 *
 *      (c) 2012 Daniel Fiala <danfiala@ucw.cz>
 *
 */

package com.logentries.re2;

public final class RE2 extends LibraryLoader implements AutoCloseable {
    private static native long compileImpl(final String pattern, final Options options) throws RegExprException;
    private static native void releaseImpl(final long pointer);
    private static native boolean fullMatchImpl(final String str, final long pointer, Object ... args);
    private static native boolean partialMatchImpl(final String str, final long pointer, Object ... args);

    private static native boolean fullMatchImpl(final String str, final String pattern, Object ... args);
    private static native boolean partialMatchImpl(final String str, final String pattern, Object ... args);

    private static native int numberOfCapturingGroupsImpl(final long pointer);

    private long pointer;

    private void checkState() throws IllegalStateException {
        if (pointer == 0) {
            throw new IllegalStateException();
        }
    }
    boolean isClosed() {
        return pointer == 0;
    }

    public RE2(final String pattern) throws RegExprException {
        this(pattern, null);
    }
    public RE2(final String pattern, final Options options) throws RegExprException {
        pointer = compileImpl(pattern, options);
    }

    public int numberOfCapturingGroups() {
        checkState();
        return numberOfCapturingGroupsImpl(pointer);
    }

    public void dispoze() {
        if (pointer != 0) {
            releaseImpl(pointer);
            pointer = 0;
        }
    }

    public void close() {
        dispoze();
    }

    protected void finalize() throws Throwable {
        dispoze();
        super.finalize();
    }

    static private int checkArg(final Object obj) throws IllegalArgumentException {
        if (obj instanceof int[]) {
            return ((int[])obj).length;
        }
        if (obj instanceof long[]) {
            return ((long[])obj).length;
        }
        if (obj instanceof float[]) {
            return ((float[])obj).length;
        }
        if (obj instanceof double[]) {
            return ((double[])obj).length;
        }
        if (obj instanceof String[]) {
            return ((String[])obj).length;
        }
        throw new IllegalArgumentException();
    }

    static private void checkArgs(Object ... args) throws IllegalArgumentException {
        int length = 0;
        for (Object arg: args) {
            if ((length += checkArg(arg)) > 31) {
                throw new IllegalArgumentException("Only up to 31 arguments supported");
            }
        }
    }

    public static boolean fullMatch(final String str, final String pattern, Object ... args) {
        checkArgs(args);
        return fullMatchImpl(str, pattern, args);
    }

    public static boolean partialMatch(final String str, final String pattern, Object ... args) {
        checkArgs(args);
        return partialMatchImpl(str, pattern, args);
    }

    public boolean fullMatch(final String str, Object ... args) throws IllegalStateException {
        checkState();
        checkArgs(args);
        return fullMatchImpl(str, pointer, args);
    }

    public boolean partialMatch(final String str, Object ... args) throws IllegalStateException {
        checkState();
        checkArgs(args);
        return partialMatchImpl(str, pointer, args);
    }

    public RE2Matcher matcher(final String str) {
        checkState();
        return new RE2Matcher(str, this, pointer);
    }
}
