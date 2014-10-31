package com.logentries.re2_test;

import com.logentries.re2.RE2;
import com.logentries.re2.RegExprException;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class TestRandomExpr {
    private static class InterruptibleCharSequence implements CharSequence {
        CharSequence inner;

        public InterruptibleCharSequence(CharSequence inner) {
            super();
            this.inner = inner;
        }

        public char charAt(int index) {
            if (Thread.interrupted()) { // clears flag if set
                throw new RuntimeException(new InterruptedException());
            }
            return inner.charAt(index);
        }

        public int length() {
            return inner.length();
        }

        public CharSequence subSequence(int start, int end) {
            return new InterruptibleCharSequence(inner.subSequence(start, end));
        }

        @Override
        public String toString() {
            return inner.toString();
        }
    }

    private List<String> mAlphabet = Arrays.asList("aaa", "b", "ccc");

    private GenRegExpr genRegExpr = new GenRegExpr(mAlphabet, 3, 12);
    private GenString genString = new GenString(mAlphabet, 15);

    private static Boolean applyMatches(final Pattern pattern, final String str) {
        class ApplyMatches implements Runnable {
            private volatile Boolean res = null;
            public Boolean getRes() {
                return res;
            }

            public void run() {
                res = pattern.matcher(new InterruptibleCharSequence(str)).matches();
            }
        }
        ApplyMatches am = new ApplyMatches();
        Thread thread = new Thread(am);
        thread.start();
        try {
            thread.join(1500);
        } catch (InterruptedException ex) {
        }
        final Boolean res = am.getRes();
        if (res == null) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException ex) {
            }
        }
        return res;
    }

    private static Boolean applyFind(final Pattern pattern, final String str) {
        class ApplyFind implements Runnable {
            private volatile Boolean res = null;
            public Boolean getRes() {
                return res;
            }

            public void run() {
                res = pattern.matcher(new InterruptibleCharSequence(str)).find();
            }
        }
        ApplyFind af = new ApplyFind();
        Thread thread = new Thread(af);
        thread.start();
        try {
            thread.join(1500);
        } catch (InterruptedException ex) {
        }
        final Boolean res = af.getRes();
        if (res == null) {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException ex) {
            }
        }
        return res;
    }

    private void compareOneRandExpr(final int index) {
        final String regExprStr = genRegExpr.next();
        System.err.println("Runnig i = " + index + "\t" + regExprStr);

        Pattern pattern = Pattern.compile(regExprStr);
        System.err.println("\t+Pattern.compile()");
        RE2 re2 = null;
        try {
            re2 = new RE2(regExprStr);
        } catch (RegExprException e) {
            System.err.println("Cannot construct re: [" + regExprStr + "] : " + e.getMessage());
            fail("Unexpected error in RE");
        }
        System.err.println("\t+new RE2()");

        for (int i = 0; i < 25; ++i) {
            final String str = genString.next();
            System.err.println("\t" + str);
            final Boolean matches = applyMatches(pattern, str);
            if (matches == null) {
                System.err.println("Timeout of matches(.) for re=[" + regExprStr + "] and string=[" + str + "]");
            }
            System.err.println("\t\tPattern.matches()");
            final boolean re2_matches = re2.fullMatch(str);
            System.err.println("\t\tRE2.matches()");
            final Boolean found = applyFind(pattern, str);
            if (found == null) {
                System.err.println("Timeout of find(.) for re=[" + regExprStr + "] and string=[" + str + "]");
            }
            System.err.println("\t\tPattern.find()");
            final boolean re2_found = re2.partialMatch(str);
            System.err.println("\t\tRE2.partialMatch()");
            if ((matches != null && matches != re2_matches) || (found != null && found != re2_found)) {
                System.err.println("reg-expr:[" + regExprStr + "]; str:[" + str + "] " + matches + "\t" + re2_matches + "\t" + found + "\t" + re2_found);
            }
            if (matches != null) {
                assertEquals(matches, re2_matches);
            }
            if (found != null) {
                assertEquals(found, re2_found);
            }
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
//                    runOneRandRE2(i);
                    compareOneRandExpr(i);
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

        
        RE2 re2 = null;
        try {
            new RE2(regExprStr);
        } catch (RegExprException e) {
            System.err.println("Cannot construct re: [" + regExprStr + "] : " + e.getMessage());
            fail("Unexpected error in RE");
        }
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
