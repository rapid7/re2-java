package com.logentries.re2;

import java.util.ArrayList;
import java.util.regex.MatchResult;

public class RE2Matcher implements MatchResult, AutoCloseable {

    private static native long createStringBuffer(final String input);
    private static native void releaseStringBuffer(final String input, final long pointer);
    private static native boolean findImpl(
        final Object matcher,
        final long re2_pointer,
        final long str_pointer,
        final int start,
        final int end
    );

    static class Range {
        int start, end;
        static Range of(int start, int end) {
            Range r = new Range();
            r.start = start;
            r.end = end;
            return r;
        }
    }

    public static void addGroup(RE2Matcher obj, int start, int end) {
        if (start >= 0 && end >= 0) {
            start = obj.utf8Offset.fromByteToString(start);
            end = obj.utf8Offset.fromByteToString(end);
        }
        obj.groups.add(Range.of(start, end));
    }


    private ArrayList<Range> groups;
    private String input;
    private long utf8StringPointer = 0;
    private long re2Pointer = 0;
    private RE2 regex;
    private UTF8StringOffset utf8Offset;
    private boolean matched;

    RE2Matcher(String input, RE2 regex, long re2Pointer) {
        this.input = input;
        this.matched = false;
        this.groups = new ArrayList<>(regex.numberOfCapturingGroups() + 1);
        this.utf8StringPointer = createStringBuffer(input);
        this.utf8Offset = new UTF8StringOffset(input);
        this.re2Pointer = re2Pointer;
        this.regex = regex; //to avoid that re2Pointer could be garbaged
    }

    private void free() {
        if (utf8StringPointer != 0) {
            releaseStringBuffer(input, utf8StringPointer);
            utf8StringPointer = 0;
        }
    }
    public void close() {
        free();
    }

    @Override
    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }

    public boolean found() {
        return matched;
    }

    public boolean findNext() {
        if (!matched) return find();
        else return find(end(0));
    }

    public boolean find() {
        return find(0);
    }
    public boolean find(int start) {
        return find(start, input.length());
    }
    public boolean find(int start, int end) {
        groups.clear();
        matched = false;

        if (utf8StringPointer == 0) throw new IllegalStateException("Matcher has been already closed");
        if (regex.isClosed()) throw new IllegalStateException("Regex has been already closed");

        start = utf8Offset.fromStringToByte(start);
        end = utf8Offset.fromStringToByte(end);
        return matched = findImpl(this, re2Pointer, utf8StringPointer, start, end);
    }

    private void checkGroup(int group) {
        if (!matched) throw new IllegalStateException("The pattern has not been matched!");
        if (group >= groups.size()) throw new IllegalStateException("Group n. "+group+" is not in pattern!");
    }

    @Override
    public int start() {
        return start(0);
    }

    @Override
    public int start(int group) {
        checkGroup(group);
        return groups.get(group).start;
    }

    @Override
    public int end() {
        return end(0);
    }

    @Override
    public int end(int group) {
        checkGroup(group);
        return groups.get(group).end;
    }

    @Override
    public String group() {
        return group(0);
    }

    @Override
    public String group(int group) {
        checkGroup(group);
        if (groups.get(group).start < 0)
            return null;
        else
            return input.subSequence(groups.get(group).start, groups.get(group).end).toString();
    }

    @Override
    public int groupCount() {
        checkGroup(0);
        return groups.size();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(matched);
        for (int i=0; i<groups.size(); i++) {
            buffer.append(String.format("\n%3d [%4d,%4d] %s", i, start(i), end(i), group(i)));
        }
        return buffer.toString();
    }
}
