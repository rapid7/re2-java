package com.logentries.re2;

public class RE2 {
    private static native long compileImpl(final String pattern, final Options options);
    private static native void releaseImpl(final long pointer);
    private static native boolean fullMatchImpl(final String str, final long pointer, Object ... args);
    private static native boolean partialMatchImpl(final String str, final long pointer, Object ... args);

    private static native boolean fullMatchImpl(final String str, final String pattern, Object ... args);
    private static native boolean partialMatchImpl(final String str, final String pattern, Object ... args);

    private long pointer;

    private void check_state() throws IllegalStateException {
        if (pointer == 0) {
            throw new IllegalStateException();
        }
    }

    public RE2(final String pattern) {
        this(pattern, null);
    }
    public RE2(final String pattern, final Options options) {
        pointer = compileImpl(pattern, options);
    }
    static {
        System.loadLibrary("re2-java"); 
    }

    public void dispoze() {
        if (pointer != 0) {
            releaseImpl(pointer);
            pointer = 0;
        }
    }

    public static boolean fullMatch(final String str, final String pattern, Object ... args) {
        return fullMatchImpl(str, pattern, args);
    }

    public static boolean partialMatch(final String str, final String pattern, Object ... args) {
        return partialMatchImpl(str, pattern, args);
    }

    public boolean fullMatch(final String str, Object ... args) throws IllegalStateException {
        check_state();
        return fullMatchImpl(str, pointer, args);
    }

    public boolean partialMatch(final String str, Object ... args) throws IllegalStateException {
        check_state();
        return partialMatchImpl(str, pointer, args);
    }
}
