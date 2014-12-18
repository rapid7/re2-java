package com.logentries.re2.entity;

/**
 * Name, matching text and the location of that text.
 */
public class NamedGroup {
    public final String name;
    public final CaptureGroup captureGroup;

    public NamedGroup(final String name, final CaptureGroup captureGroup) {
        this.name = name;
        this.captureGroup = captureGroup;
    }

    public NamedGroup(final String name, final String matchingText, final int start, final int end) {
        this(name, new CaptureGroup(matchingText, start, end));
    }
}
