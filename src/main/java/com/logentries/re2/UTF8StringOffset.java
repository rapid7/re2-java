package com.logentries.re2;

import java.nio.charset.StandardCharsets;

public class UTF8StringOffset {

    static float AVG_BYTE_PER_CHAR = StandardCharsets.UTF_8.newEncoder().averageBytesPerChar();

    private int[] byte2string;
    private int byteSize;
    private int[] string2byte;
    private int stringSize;
    public UTF8StringOffset(String input) {
        string2byte = new int[input.length()];
        stringSize = input.length();
        byte2string = new int[(int)(input.length() * AVG_BYTE_PER_CHAR)];
        byteSize = 0;
        for (int i=0; i<input.length(); i++) {

            char c = input.charAt(i);
            int slop;
            // see sun.nio.cs.UTF_8.encodeArrayLoop
            if (c < 128) slop = 1;
            else if (c < 2048) slop = 2;
            else if (Character.isSurrogate(c)) {
                i++;
                slop = 4;
            } else {
                slop = 3;
            }

            if (byte2string.length - byteSize < slop) {
                int [] newbie = new int[Math.max(byte2string.length * 2, slop)]; //if slop=3 and length=1 ... e.g. "€"
                System.arraycopy(byte2string,0,newbie,0, byteSize);
                byte2string = newbie;
            }

            string2byte[i] = byteSize;

            int strPos = byteSize ==0 ? 0 : byte2string[byteSize -1] + 1;
            for (int k = 0; k < slop; k++) byte2string[byteSize+k] = strPos;
            byteSize += slop;

        }
        
    }

    public int fromByteToString(int bytePos) {
        if (bytePos < 0) throw new IndexOutOfBoundsException(""+bytePos);
        if (bytePos > byteSize) throw new IndexOutOfBoundsException(""+bytePos);
        if (bytePos == byteSize) return stringSize;
        else return byte2string[bytePos];
    }

    public int fromStringToByte(int strPos) {
        if (strPos < 0) throw new IndexOutOfBoundsException(""+strPos);
        if (strPos > stringSize) throw new IndexOutOfBoundsException(""+strPos);
        if (strPos == stringSize) return byteSize;
        else return string2byte[strPos];
    }
    
}
