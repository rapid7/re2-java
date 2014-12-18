package com.logentries.re2.entity;

/**
 * Matching text and the location of that text.
 */
public class CaptureGroup {
    public final int start, end;
    public final String matchingText;

    public CaptureGroup(final String matchingText, final int start, final int end) {
        this.matchingText = matchingText;
        this.start = start;
        this.end = end;
    }
}
