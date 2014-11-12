package com.logentries.re2;

/**
 * Created by scaiella on 12/11/14.
 */
public class RE2String implements CharSequence, AutoCloseable {

    private static native long createStringBuffer(final String input);
    private static native void releaseStringBuffer(final String input, final long pointer);


    private CharSequence input;
    private String inputString;
    private long utf8StringPointer = 0;
    private UTF8CharOffset utf8Offset;


    public RE2String(CharSequence input) {
        this.input = input;
        this.inputString = input.toString();
        this.utf8StringPointer = createStringBuffer(inputString);
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
            releaseStringBuffer(inputString, utf8StringPointer);
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
