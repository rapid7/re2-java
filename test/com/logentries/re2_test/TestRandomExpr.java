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

    private void compareOneRandExpr() {
        final String regExprStr = genRegExpr.next();

        Pattern pattern = Pattern.compile(regExprStr);
        RE2 re2 = new RE2(regExprStr);

        for (int i = 0; i < 25; ++i) {
            final String str = genString.next();
            final boolean matches = pattern.matcher(str).matches();
            final boolean re2_matches = re2.fullMatch(str);
            final boolean found = pattern.matcher(str).find();
            final boolean re2_found = re2.partialMatch(str);
            if (matches != re2_matches || found != re2_found) {
                System.err.println("reg-expr:[" + regExprStr + "]; str:[" + str + "] " + matches + "\t" + re2_matches + "\t" + found + "\t" + re2_found);
            }
            assertEquals(matches, re2_matches);
            assertEquals(found, re2_found);
        }
        re2.dispoze();
    }

    @Test
    public void testRandExpr() {
        for (int i = 0; i < 200; ++i) {
            System.err.println("Runnig i = " + i);
            compareOneRandExpr();
        }
    }
}
