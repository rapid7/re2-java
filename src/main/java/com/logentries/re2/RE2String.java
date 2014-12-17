package com.logentries.re2;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;

public class RE2String implements CharSequence, AutoCloseable {

    private static native long createStringBuffer(final byte[] input);
    private static native void releaseStringBuffer(final byte[] input, final long pointer);

    private CharSequence input;
    private byte[] utf8CString;
    private long utf8StringPointer = 0;
    private UTF8CharOffset utf8Offset;


    public RE2String(CharSequence input) {
        this.input = input;
        try {
            this.utf8CString = createUtf8CString(input);
        } catch (Exception e ){
            throw new IllegalArgumentException("Unable to encode input using UTF-8", e);
        }
        this.utf8StringPointer = createStringBuffer(utf8CString);
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
     * @deprecated
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
            releaseStringBuffer(utf8CString, utf8StringPointer);
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
        return input.toString();
    }

    private byte[] createUtf8CString(CharSequence s) throws Exception {
        CharsetEncoder encoder = StandardCharsets.UTF_8.newEncoder()
                .onMalformedInput(CodingErrorAction.REPLACE)
                .onUnmappableCharacter(CodingErrorAction.REPLACE);

        ByteBuffer bytes = encoder.encode(CharBuffer.wrap(s));

        if (bytes.limit() == bytes.capacity()) {
            ByteBuffer newBuffer = ByteBuffer.allocate(bytes.limit()+1);
            System.arraycopy(bytes.array(), 0, newBuffer.array(), 0, bytes.limit());
            bytes = newBuffer;
        } else
            bytes.limit(bytes.limit()+1);

        bytes.put(bytes.limit()-1, (byte) 0);
        return bytes.array();
    }
}
