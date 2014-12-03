package com.logentries.re2;

import java.nio.charset.StandardCharsets;

/**
 * Created by scaiella on 12/11/14.
 */
public class RE2String implements CharSequence, AutoCloseable {

    public static native long createStringBuffer(final byte[] input);
    private static native void releaseStringBuffer(final byte[] input, final long pointer);

    private CharSequence input;
    private String inputString;
    private byte[] utf8String;
    private long utf8StringPointer = 0;
    private UTF8CharOffset utf8Offset;


    public RE2String(CharSequence input) {
        this.input = input;
        this.inputString = input.toString();
        try {
            this.utf8String = inputString.getBytes(StandardCharsets.UTF_8);
        } catch (Exception e ){
            throw new IllegalArgumentException("Unable to encode input using UTF-8", e);
        }
        this.utf8StringPointer = createStringBuffer(utf8String);
        this.utf8Offset = new UTF8CharOffset(input);
    }

    public int bytePos(int charPosition) {
        check();
        return utf8Offset.fromStringToByte(charPosition);
    }
    public int charPos(int bytePosition) {
        check();
        return utf8Offset.fromByteToChar(bytePosition);
    }
    public boolean isClosed() {
        return utf8StringPointer == 0;
    }

    /**
     * @deprecated You're doing it wrong! Do your fucking business!
     */
    @Deprecated()
    long pointer() {
        return utf8StringPointer;
    }


    private void check() {
        if (utf8StringPointer == 0)
            throw new IllegalStateException("Buffer has been already closed!");
    }

    private void free() {
        if (utf8StringPointer != 0) {
            releaseStringBuffer(utf8String, utf8StringPointer);
            utf8StringPointer = 0;
        }
    }
    @Override
    public void close() {
        free();
    }

    @Override
    protected void finalize() throws Throwable {
        free();
        super.finalize();
    }


    @Override
    public int length() {
        return input.length();
    }

    @Override
    public char charAt(int index) {
        return input.charAt(index);
    }

    @Override
    public CharSequence subSequence(int start, int end) {
        return input.subSequence(start, end);
    }

    @Override
    public String toString() {
        return inputString;
    }
}
