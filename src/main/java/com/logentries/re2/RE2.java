package com.logentries.re2;

// Note: AutoCloseable is available since Java 7 for support of try-with-resources statement
//import java.lang.AutoCloseable;

public class RE2 /* implements AutoCloseable */ {
    private static native long compileImpl(final String pattern, final Options options) throws RegExprException;
    private static native void releaseImpl(final long pointer);
    private static native boolean fullMatchImpl(final String str, final long pointer, Object ... args);
    private static native boolean partialMatchImpl(final String str, final long pointer, Object ... args);

    private static native boolean fullMatchImpl(final String str, final String pattern, Object ... args);
    private static native boolean partialMatchImpl(final String str, final String pattern, Object ... args);

    private long pointer;

    private void checkState() throws IllegalStateException {
        if (pointer == 0) {
            throw new IllegalStateException();
        }
    }

    public RE2(final String pattern) throws RegExprException {
        this(pattern, null);
    }
    public RE2(final String pattern, final Options options) throws RegExprException {
        pointer = compileImpl(pattern, options);
    }
    static {
        if (!EmbeddedLibraryTools.LOADED_RE2) {
            System.loadLibrary("re2");
        }
        if (!EmbeddedLibraryTools.LOADED_RE2_JAVA) {
            System.loadLibrary("re2-java");
        }
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
}
