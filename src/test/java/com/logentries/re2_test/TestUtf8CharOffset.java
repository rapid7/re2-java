package com.logentries.re2_test;

import com.logentries.re2.UTF8CharOffset;
import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class TestUtf8CharOffset {

    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        // input            char -> byte            byte -> char

        return asList(new Object[][]{{
            "abcd efg", asList(0,1,2,3,4,5,6,7), asList(0,1,2,3,4,5,6,7),
        },{
            "abcd èfg", asList(0,1,2,3,4,5,7,8), asList(0,1,2,3,4,5,5,6,7)
        },{
            "abcd €fg", asList(0,1,2,3,4,5,8,9), asList(0,1,2,3,4,5,5,5,6,7)
        },{
            "abcd €€€", asList(0,1,2,3,4,5,8,11), asList(0,1,2,3,4,5,5,5,6,6,6,7,7,7)
        },{
            "àbcd €fg", asList(0,2,3,4,5,6,9,10), asList(0,0,1,2,3,4,5,5,5,6,7)
        },{
            "a\uD83D\uDC36cd efg", asList(0,1,1,5,6,7,8,9,10), asList(0,1,1,1,1,3,4,5,6,7,8)
        },{
            // but why on earth we have to spend time to support chars!!!!!
            "\uD83D\uDC36\uD83D\uDC3Ecd efg", asList(0,0,4,4,8,9,10,11,12,13), asList(0,0,0,0,2,2,2,2,4,5,6,7,8,9)
        },{
            "\uD83D\uDD0D sp", asList(0,0,4,5,6), asList(0,0,0,0,2,3,4)
        }});
    }

    @Parameterized.Parameter(value = 0)
    public String input;
    @Parameterized.Parameter(value = 1)
    public List<Integer> char2byte;
    @Parameterized.Parameter(value = 2)
    public List<Integer> byte2char;

    @Test
    public void test() throws Exception {

        UTF8CharOffset offset = new UTF8CharOffset(input);
        byte[] utf8 = input.getBytes("UTF-8");

        assertEquals("check test consistency: ", utf8.length, byte2char.size());
        assertEquals("check test consistency: ", input.length(), char2byte.size());

        List<Integer> myChar2Byte = new ArrayList<>();
        for (int i=0; i<input.length(); i++) myChar2Byte.add(offset.fromStringToByte(i));
        List<Integer> myByte2Char = new ArrayList<>();
        for (int i=0; i<utf8.length; i++) myByte2Char.add(offset.fromByteToChar(i));

        assertThat("Char mapping: "+input, myChar2Byte, equalTo(char2byte));
        assertThat("Byte mapping: "+input, myByte2Char, equalTo(byte2char));

        //test string to byte
        Charset cs = StandardCharsets.UTF_8;
        for (int i = 0; i < input.length(); ) {
            final int codepoint = input.codePointAt(i);
            ByteBuffer expected = cs.encode(CharBuffer.wrap(input, i, i + Character.charCount(codepoint)));
            for (int j=0; j<expected.limit(); j++)
                assertEquals("Byte mis-aligned at char " + i, expected.get(j), utf8[offset.fromStringToByte(i) + j]);
            i += Character.charCount(codepoint);
        }

        //test byte to string
        for (int i=0; i < utf8.length; i++) {
            int j=i;
            while (j<utf8.length && offset.fromByteToChar(i) == offset.fromByteToChar(j)) j++;
            CharBuffer cb = cs.decode(ByteBuffer.wrap(Arrays.copyOfRange(utf8, i, j)));
            int expected = cb.toString().codePointAt(0);
            int actual = input.codePointAt(offset.fromByteToChar(i));
            assertEquals("Char mis-aligned at byte block " + i, expected, actual);
            i = j-1 ;
        }
    }
}
