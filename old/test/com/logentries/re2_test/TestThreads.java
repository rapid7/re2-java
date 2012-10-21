package com.logentries.re2_test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import com.logentries.re2.RE2;
import com.logentries.re2.Options;

public class TestThreads {
    @Test
    public void testThreads() {
        class Worker implements Runnable {
            public void xxx() {
                final boolean res1 = RE2.fullMatch("hello", "(h.*o)");
                assertTrue(res1);
                /* */
                final boolean res2 = RE2.fullMatch("hello", "(h.*x)");
                assertFalse(res2);
                /* */
                final RE2 re_x = new RE2("(h.*o)");
                assertNotNull(re_x);
                final boolean res3 = re_x.fullMatch("hello");
                assertTrue(res3);
                final boolean res4 = re_x.fullMatch("hellx");
                assertFalse(res4);
                re_x.dispoze();
                final RE2 re_y = new RE2("(h.*o)", new Options());
                assertNotNull(re_y);
                re_y.dispoze();
                /* */
                int[] out00 = new int[1];
                final boolean res5 = RE2.fullMatch("1256", "(\\d+)", out00);
                assertTrue(res5);
                assertEquals(1256, out00[0]);
                /* */
                int[] out10 = new int[1];
                String[] out11 = new String[1];
                long[] out12 = new long[1];
                final boolean res6 = RE2.fullMatch("1256xsssx136985478256", "(\\d+)x(\\w+)x(\\d+)", out10, out11, out12);
                assertTrue(res6);
                assertEquals(1256, out10[0]);
                assertEquals("sss", out11[0]);
                assertEquals(136985478256L, out12[0]);
                /* */
                int[] out20 = new int[3];
                String[] out21 = new String[2];
                long[] out22 = new long[4];
                final boolean res7 = RE2.fullMatch("1256-34-567xstring-everythingworks@13-698-547-12345678256", "(\\d+)-(\\d+)-(\\d+)x(\\w+)-(\\w+)@(\\d+)-(\\d+)-(\\d+)-(\\d+)", out20, out21, out22);
                assertTrue(res7);
                // out20
                assertEquals(1256, out20[0]); 
                assertEquals(34, out20[1]);
                assertEquals(567, out20[2]);
                // out21
                assertEquals("string", out21[0]);
                assertEquals("everythingworks", out21[1]);
                // out22
                assertEquals(13L, out22[0]);
                assertEquals(698L, out22[1]);
                assertEquals(547L, out22[2]);
                assertEquals(12345678256L, out22[3]);
            }
            public void run() {
                for (int i = 0; i < 2000; ++i) {
                    xxx();
                }
            }
        }

        Thread[] ths = new Thread[12];
        for (int i = 0; i < ths.length; ++i) {
            (ths[i] = new Thread(new Worker())).start();
        }
        for (int i = 0; i < ths.length; ++i) {
            try {
                ths[i].join();
            } catch (InterruptedException e) {
            }
        }
    }
}
