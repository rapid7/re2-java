package com.logentries.re2_test;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import com.logentries.re2.RE2;
import com.logentries.re2.Options;
import java.util.List;
import java.util.Arrays;

import java.util.regex.Pattern;

public class TestRandomExpr {
    private List<String> mAlphabet = Arrays.asList("aaa", "b", "ccc");

    private GenRegExpr genRegExpr = new GenRegExpr(mAlphabet, 3, 12);
    private GenString genString = new GenString(mAlphabet, 15);

    private void compareOneRandExpr(final int index) {
        final String regExprStr = genRegExpr.next();
        System.err.println("Runnig i = " + index + "\t" + regExprStr);

        Pattern pattern = Pattern.compile(regExprStr);
        System.err.println("\t+Pattern.compile()");
        RE2 re2 = new RE2(regExprStr);
        System.err.println("\t+new RE2()");

        for (int i = 0; i < 25; ++i) {
            final String str = genString.next();
            System.err.println("\t" + str);
            final boolean matches = pattern.matcher(str).matches();
            System.err.println("\t\tPattern.matches()");
            final boolean re2_matches = re2.fullMatch(str);
            System.err.println("\t\tRE2.matches()");
            final boolean found = pattern.matcher(str).find();
            System.err.println("\t\tPattern.find()");
            final boolean re2_found = re2.partialMatch(str);
            System.err.println("\t\tRE2.partialMatch()");
            if (matches != re2_matches || found != re2_found) {
                System.err.println("reg-expr:[" + regExprStr + "]; str:[" + str + "] " + matches + "\t" + re2_matches + "\t" + found + "\t" + re2_found);
            }
            assertEquals(matches, re2_matches);
            assertEquals(found, re2_found);
        }
        re2.dispoze();
    }

    public void testRandExpr() {
        for (int i = 0; i < 200; ++i) {
            compareOneRandExpr(i);
        }
    }

    @Test
    public void testRandRE2() {
        class Worker implements Runnable {
            public void run() {
                for (int i = 0; i < 2000; ++i) {
                    runOneRandRE2(i);
                }
            }
        }

        Thread[] ths = new Thread[8];
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

    public void runOneRandRE2(final int index) {
        final String regExprStr = genRegExpr.next();
        System.err.println("Runnig i = " + index + "\t" + regExprStr);

        RE2 re2 = new RE2(regExprStr);
        System.err.println("\t+new RE2()");

        for (int i = 0; i < 25; ++i) {
            final String str = genString.next();
            System.err.println("\t" + str);
            final boolean re2_matches = re2.fullMatch(str);
            System.err.println("\t\tRE2.matches()");
            final boolean re2_found = re2.partialMatch(str);
            System.err.println("\t\tRE2.partialMatch()");
            System.err.println("reg-expr:[" + regExprStr + "]; str:[" + str + "] " + re2_matches + "\t" + re2_found);
        }
        re2.dispoze();
    }
}
