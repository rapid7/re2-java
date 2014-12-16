/*
 *      Java Bindings for the RE2 Library
 *
 *      (c) 2012 Daniel Fiala <danfiala@ucw.cz>
 *
 */

package com.logentries.re2;

import com.logentries.re2.entity.CaptureGroup;
import com.logentries.re2.entity.NamedGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.MatchResult;

public final class RE2 extends LibraryLoader implements AutoCloseable {
    private static native long compileImpl(final String pattern, final Options options) throws RegExprException;
    private static native void releaseImpl(final long pointer);
    private static native boolean fullMatchImpl(final String str, final long pointer, Object ... args);
    private static native boolean partialMatchImpl(final String str, final long pointer, Object ... args);
    private static native boolean fullMatchImpl(final String str, final String pattern, Object ... args);
    private static native boolean partialMatchImpl(final String str, final String pattern, Object ... args);
    private static native List<String> getCaptureGroupNamesImpl(final long pointer, Object ... args);
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

    public RE2(final String pattern, final Options options) throws RegExprException {
        pointer = compileImpl(pattern, options);
    }
    public RE2(final String pattern, final Options.Flag... options) throws RegExprException {
        Options opt = new Options();
        for (Options.Flag f : options) f.apply(opt);
        pointer = compileImpl(pattern, opt);
    }

    public static RE2 compile(final String pattern, final Options.Flag... options) {
        try {
            return new RE2(pattern, options);
        } catch (RegExprException ree) {
            throw new IllegalArgumentException(ree);
        }
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

    /**
     * This method returns ordered names.
     *
     * @param args
     * @return List of names for the capture groups
     * @throws IllegalStateException
     */
    public List<String> getCaptureGroupNames(Object... args) throws IllegalStateException {
        checkState();
        checkArgs(args);
        return getCaptureGroupNamesImpl(pointer, args);
    }

    public RE2Matcher matcher(final CharSequence str) {
        return matcher(str, true);
    }
    public RE2Matcher matcher(final CharSequence str, boolean fetchGroups) {
        checkState();
        return new RE2Matcher(str, this, pointer, fetchGroups);
    }
    public RE2Matcher matcher(final RE2String str) {
        return matcher(str, true);
    }
    public RE2Matcher matcher(final RE2String str, boolean fetchGroups) {
        checkState();
        return new RE2Matcher(str, this, pointer, fetchGroups);
    }

    /**
     * Gets the ordered capture groups for this event and pattern.
     * @param str is an event.
     * @return is a list of CaptureGroups.
     */
    public List<CaptureGroup> getCaptureGroups(final String str) {
        checkState();
        List<CaptureGroup> captureGroups = new ArrayList<>();
        RE2Matcher re2match = this.matcher(str);

        for (MatchResult match : re2match) {
            CaptureGroup captureGroup = new CaptureGroup(match.group(), match.start(), match.end());
            captureGroups.add(captureGroup);
        }
        return captureGroups;
    }

    /**
     * Returns a list of named capture groups and their position information in the event.
     * @param str is an event.
     * @return is a list of named capture groups.
     */
    public List<NamedGroup> getNamedCaptureGroups(final String str) {
        List<NamedGroup> namedGroups = new ArrayList<>();
        List<CaptureGroup> captureGroups = new ArrayList(getCaptureGroups(str));
        List<String> names = new ArrayList(getCaptureGroupNames());
        int len = names.size();

        if (len != captureGroups.size()) {
            throw new IllegalStateException("list of names and capture groups not same length");
        }

        for (int i = 0; i < len; i++) {
            namedGroups.add(new NamedGroup(names.get(i), captureGroups.get(i)));
        }
        return namedGroups;
    }

}
